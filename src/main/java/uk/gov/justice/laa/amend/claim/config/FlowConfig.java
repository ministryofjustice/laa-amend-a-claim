package uk.gov.justice.laa.amend.claim.config;

import java.util.List;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.config.AbstractFlowConfiguration;
import org.springframework.webflow.config.FlowExecutorBuilder;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;
import org.springframework.webflow.mvc.servlet.FlowHandlerMapping;

@Configuration
public class FlowConfig extends AbstractFlowConfiguration {

  @Bean
  public ConversionService webFlowConversionService() {
    var springConversionService = new DefaultFormattingConversionService();
    return new DefaultConversionService(springConversionService);
  }

  @Bean
  public FlowBuilderServices flowBuilderServices(
      LocalValidatorFactoryBean localValidatorFactoryBean,
      ConversionService webFlowConversionService,
      ViewFactoryCreator viewFactoryCreator) {
    return getFlowBuilderServicesBuilder()
        .setConversionService(webFlowConversionService)
        .setViewFactoryCreator(viewFactoryCreator)
        .setValidator(localValidatorFactoryBean)
        .build();
  }

  @Bean
  public FlowDefinitionRegistry flowRegistry(FlowBuilderServices flowBuilderServices) {
    return getFlowDefinitionRegistryBuilder()
        .setBasePath("classpath:flows")
        .addFlowLocationPattern("/**/*-flow.xml")
        .setFlowBuilderServices(flowBuilderServices)
        .build();
  }

  @Bean
  public FlowExecutor flowExecutor(
      FlowDefinitionRegistry flowRegistry,
      AppConfig appConfig,
      FeatureFlagsConfig featureFlagsConfig) {
    return new FlowExecutorBuilder(flowRegistry)
        .addFlowExecutionListener(
            new FlowExecutionListener() {
              @Override
              public void viewRendering(RequestContext context, View view, StateDefinition state) {
                context.getRequestScope().put("app", appConfig);
                context.getRequestScope().put("featureFlagsConfig", featureFlagsConfig);
              }
            })
        .build();
  }

  @Bean
  public FlowHandlerMapping flowHandlerMapping(FlowDefinitionRegistry flowDefinitionRegistry) {
    var handlerMapping = new FlowHandlerMapping();
    handlerMapping.setOrder(-1);
    handlerMapping.setFlowRegistry(flowDefinitionRegistry);
    return handlerMapping;
  }

  @Bean
  public FlowHandlerAdapter flowHandlerAdapter(FlowExecutor flowExecutor) {
    var handlerAdapter = new FlowHandlerAdapter();
    handlerAdapter.setFlowExecutor(flowExecutor);
    handlerAdapter.setSaveOutputToFlashScopeOnRedirect(true);
    return handlerAdapter;
  }

  @Bean
  public ViewFactoryCreator mvcViewFactoryCreator(ViewResolver thymeleafViewResolver) {
    var factoryCreator = new MvcViewFactoryCreator();
    factoryCreator.setViewResolvers(List.of(thymeleafViewResolver));
    factoryCreator.setUseSpringBeanBinding(true);
    return factoryCreator;
  }
}
