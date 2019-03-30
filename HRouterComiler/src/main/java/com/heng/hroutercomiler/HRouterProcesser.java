package com.heng.hroutercomiler;

import com.google.auto.service.AutoService;
import com.heng.routerannotation.HRouterConfig;
import com.heng.routerannotation.HRouterInterceptors;
import com.heng.routerannotation.HRouterPermission;
import com.heng.routerannotation.HRouterRule;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.net.URI;
import java.util.*;

@AutoService(Processor.class)
public class HRouterProcesser extends AbstractProcessor {

    private ClassName ruleDataClassName = ClassName.bestGuess(Constants.CLASSNAME_HROUTE_RULE);

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        UtilMgr.getMgr().init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        BasicConfigurations basicConfigurations = processRouteConfig(roundEnv);
        processRouteRules(roundEnv, basicConfigurations);
        return true;

    }

    private BasicConfigurations processRouteConfig(RoundEnvironment roundEnv) {
        TypeElement type = null;
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(HRouterConfig.class);
        Iterator<? extends Element> iterator = elements.iterator();
        BasicConfigurations configurations = null;
        while (iterator.hasNext()) {
            type = (TypeElement) iterator.next();
            if (configurations != null) {
                throw new RuntimeException("The RouteConfig in this module was defined duplicated!");
            }
            if (!Utils.isSuperClass(type, Constants.CLASSNAME_APPLICATION)) {
                throw new RuntimeException("The class you are annotated by RouteConfig must be a Application");
            }
            HRouterConfig config = type.getAnnotation(HRouterConfig.class);
            configurations = new BasicConfigurations(config);
        }
        return configurations == null ? new BasicConfigurations(null) : configurations;

    }

    private void processRouteRules(RoundEnvironment roundEnv, BasicConfigurations config) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(HRouterRule.class);
        if (elements.isEmpty()) {
            return;
        }
        TypeElement type;
        try {
            ClassName creator = ClassName.bestGuess(Constants.CLASSNAME_HROUTE_RULE_CREATOR);
            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(ClassName.get(config.pack, "HRouterRuleCreatorImpl"))
                    .addSuperinterface(creator)
                    .addModifiers(Modifier.PUBLIC);
            MethodSpec.Builder methodCreateHRouterRule = MethodSpec.overriding(getOverrideMethod(creator, Constants.METHODNAME_CREATE_HROUTER_RULE));
            methodCreateHRouterRule.addStatement("$T<String,$T> routes = new $T<>()", Map.class, ruleDataClassName, HashMap.class);
            methodCreateHRouterRule.addStatement("$T ruleData", ruleDataClassName);
            for (Element ele : elements) {
                type = (TypeElement) ele;
                if (!Utils.checkTypeValid(type)) continue;
                HRouterRule rule = type.getAnnotation(HRouterRule.class);
                methodCreateHRouterRule.addStatement("ruleData = new $T($S)", ruleDataClassName, type.getQualifiedName());
                TypeMirror[] typeMirrors = getInterceptName(type.getAnnotation(HRouterInterceptors.class));
                if (typeMirrors != null && typeMirrors.length > 0) {
                    addInterceptorsCode(methodCreateHRouterRule, typeMirrors);
                }
                HRouterPermission hRouterPermission = type.getAnnotation(HRouterPermission.class);
                if (hRouterPermission != null && hRouterPermission.value().length > 0) {
                    addPermissionCode(methodCreateHRouterRule, hRouterPermission.value());
                }
                for (String route : rule.value()) {
                    String baseUrl = config.baseUrl;
                    URI uri = URI.create(route);
                    String scheme = uri.getScheme();
                    if (Utils.isEmpty(scheme)) {
                        if (Utils.isEmpty(baseUrl)) {
                            throw new IllegalArgumentException("Could not find baseUrl set by RouteConfig to join with the route:" + route);
                        }
                        route = baseUrl + route;
                    }
                    methodCreateHRouterRule.addStatement("routes.put($S,ruleData)", route);
                }
            }
            methodCreateHRouterRule.addStatement("return routes");
            TypeSpec.Builder typeSpec = typeBuilder.addMethod(methodCreateHRouterRule.build());
            JavaFile.Builder javaBuilder = JavaFile.builder(config.pack, typeSpec.build());
            javaBuilder.build().writeTo(UtilMgr.getMgr().getFiler());
        } catch (Throwable e) {
            error(e.getMessage(), e);
        }
    }

    private void addPermissionCode(MethodSpec.Builder methodCreateHRouterRule, String[] value) {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("ruleData.setPermissions(");
        for (int i = 0; i < value.length; i++) {
            if (i > 0) {
                builder.add(",");
            }
            builder.add("$S", value[i]);
        }
        builder.add(");\r\n");
        methodCreateHRouterRule.addCode(builder.build());
    }

    private void addInterceptorsCode(MethodSpec.Builder methodCreateHRouterRule, TypeMirror[] interceptors) {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("ruleData.setInterceptors(");
        for (int i = 0; i < interceptors.length; i++) {
            if (i > 0) {
                builder.add(",");
            }
            builder.add("$T.class", interceptors[i]);
        }
        builder.add(");\r\n");
        methodCreateHRouterRule.addCode(builder.build());
    }

    private TypeMirror[] getInterceptName(HRouterInterceptors annotation) {
        if (annotation == null) return null;
        try {
            annotation.value(); // this should throw
        } catch (MirroredTypesException mte) {
            return mte.getTypeMirrors().toArray(new TypeMirror[0]);
        }
        return null;
    }

    private ExecutableElement getOverrideMethod(ClassName creator, String methodName) {
        TypeElement element = UtilMgr.getMgr().getElementUtils().getTypeElement(creator.toString());
        List<? extends Element> elements = element.getEnclosedElements();
        for (Element ele : elements) {
            if (ele.getKind() != ElementKind.METHOD) continue;
            if (methodName.equals(ele.getSimpleName().toString())) {
                return (ExecutableElement) ele;
            }
        }
        throw new RuntimeException("method createHRouterRule of interface HRouterRuleCreator not found");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(HRouterRule.class.getCanonicalName());
        types.add(HRouterConfig.class.getCanonicalName());
        return types;
    }

    private void error(String msg, Object... args) {
        UtilMgr.getMgr().getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void log(String msg, Object... args) {
        UtilMgr.getMgr().getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}
