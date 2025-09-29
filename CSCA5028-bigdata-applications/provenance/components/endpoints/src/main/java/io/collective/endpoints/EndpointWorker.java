package io.collective.endpoints;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.collective.articles.ArticleDataGateway;
import io.collective.restsupport.RestTemplate;
import io.collective.workflow.Worker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EndpointWorker implements Worker<EndpointTask> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate template;
    private final ArticleDataGateway gateway;

    public EndpointWorker(RestTemplate template, ArticleDataGateway gateway) {
        this.template = template;
        this.gateway = gateway;
    }

    @NotNull
    @Override
    public String getName() {
        return "ready";
    }

    @Override
    public void execute(EndpointTask task) throws IOException {
        String response = template.get(task.getEndpoint(), task.getAccept());
        gateway.clear();

        try {
            XmlMapper xmlMapper = new XmlMapper();
            RSS rss = xmlMapper.readValue(response, RSS.class);

            if (rss != null && rss.getChannel() != null && rss.getChannel().getItems() != null) {
                rss.getChannel().getItems().forEach(item -> {
                    String title = item.getTitle();
                    if (title != null) {
                        gateway.save(title);
                    }
                });
            } else {
                logger.error("RSS feed is missing required fields");
            }
        } catch (Exception e) {
            logger.error("Error parsing or processing RSS response", e);
        }
    }
}
