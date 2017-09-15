import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
class ExternalResourcesWebConfigurer extends WebMvcConfigurerAdapter {

  private static final Logger log = LoggerFactory.getLogger(ExternalResourcesWebConfigurer.class);

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    log.info("Adding a new resource handler for path /perso/**");
    registry.addResourceHandler("/perso/**").addResourceLocations("file:D:/entorno/ide/workspace/sso-cas/dist/extranets/perso");
  }

}