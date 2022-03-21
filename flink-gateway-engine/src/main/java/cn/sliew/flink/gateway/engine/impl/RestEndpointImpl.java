package cn.sliew.flink.gateway.engine.impl;

import cn.sliew.flink.gateway.engine.RestEndpoint;
import cn.sliew.milky.common.util.JacksonUtil;
import org.apache.flink.calcite.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.runtime.rest.messages.DashboardConfiguration;
import org.apache.flink.runtime.rest.messages.dataset.ClusterDataSetListResponseBody;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;

public class RestEndpointImpl implements RestEndpoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String webInterfaceURL;

    public RestEndpointImpl(String webInterfaceURL) {
        this.webInterfaceURL = webInterfaceURL;
    }

    @Override
    public boolean cluster() throws IOException {
        HttpResponse response = Request.delete(webInterfaceURL + "/cluster")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnResponse();
        return response.getCode() == HttpStatus.SC_SUCCESS;
    }

    @Override
    public DashboardConfiguration config() throws IOException {
        Content content = Request.get(webInterfaceURL + "/config")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        // 不顶用再说
        return JacksonUtil.parseJsonString(content.asString(), DashboardConfiguration.class);
    }

    @Override
    public ClusterDataSetListResponseBody datasets() throws IOException {
        Content content = Request.get(webInterfaceURL + "/datasets")
                .connectTimeout(Timeout.ofSeconds(3L))
                .responseTimeout(Timeout.ofSeconds(3L))
                .execute().returnContent();
        return JacksonUtil.parseJsonString(content.asString(), ClusterDataSetListResponseBody.class);
    }
}
