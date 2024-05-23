package com.myclass.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

@Slf4j
public class DemoAgent {

    public static void premain(String args, Instrumentation inst) {
        log.info("Beginning premain method.");
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule, domain) -> builder
                .method(ElementMatchers.named("hello").and(ElementMatchers.isPublic()))
                .intercept(MethodDelegation.to(DemoInterceptor.class));
        // Generate the ClassFileTransformer class through AgentBuilder
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default()
                .type(ElementMatchers.named("com.myclass.agent.DemoService"))
                .transform(transformer)
                .makeRaw();
        inst.addTransformer(classFileTransformer);
        log.info("The premain method end.");
    }
}
