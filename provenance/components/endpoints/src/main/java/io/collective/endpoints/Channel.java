package io.collective.endpoints;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {
    private List<RSSItem> item;

    public List<RSSItem> getItem() {
        return item;
    }

    public void setItem(List<RSSItem> item) {
        this.item = item;
    }
}
