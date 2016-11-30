package org.springframework.cloud.servicebroker.kafka.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.*;

/**
 * Created by rhardt on 11/20/16.
 */
@Configuration
public class KafkaSBConfig {

    @Autowired
    private Environment env;

    @Bean
    public CuratorFramework curatorFramework() {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(env.getProperty("kafka.host") + ":2181", retryPolicy);
        client.start();
        return client;
    }


    @Bean
    public Catalog catalog() {
        return new Catalog(Collections.singletonList(
                new ServiceDefinition(
                        "kafka",
                        "Kafka",
                        "Kafka Service Broker",
                        true,
                        true,
                        Collections.singletonList(
                                new Plan("kafka-plan",
                                        "Default Kafka Plan",
                                        "This is a default mongo plan.  All services are created equally.",
                                        getPlanMetadata())),
                        Arrays.asList("kafka", "document"),
                        getServiceDefinitionMetadata(),
                        null, null)));
    }

/* Used by Pivotal CF console */

    private Map<String, Object> getServiceDefinitionMetadata() {
        Map<String, Object> sdMetadata = new HashMap<>();
        sdMetadata.put("displayName", "Kafka");
        //sdMetadata.put("imageUrl", "http://info.mongodb.com/rs/mongodb/images/MongoDB_Logo_Full.png");
        sdMetadata.put("imageUrl", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAACXBIWXMAAAsTAAALEwEAmpwYAAABWWlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iWE1QIENvcmUgNS40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp0aWZmPSJodHRwOi8vbnMuYWRvYmUuY29tL3RpZmYvMS4wLyI+CiAgICAgICAgIDx0aWZmOk9yaWVudGF0aW9uPjE8L3RpZmY6T3JpZW50YXRpb24+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgpMwidZAAAYYUlEQVR4Ae2dBazkNhOAfWVmxldmZr4yM6igqnRlBpXVViqD2qqk8qnMzMzMrDIzM9f/fPPLq2xe7DjZ7N67SyzlJS927JnxeDyeGXsHWUmmSbWlwEi1xbxBXCnQMEDNGaFhgIYBak6BmqPfSICGAWpOgZqj30iAhgFqToGao99IgIYBak6BmqPfSICGAWpOgZqj30iAhgFqToGao99IgIYBak6BmqPfSICGAWpOgZqj30iAhgFqToGao99IgIYBak6BmqPfSICGAWpOgZqj30iAhgFqToGao99IgIYBak6BmqPfSICaM8AoAw3/n376yXz00Ufmww8/NF999ZX59ddfzb///mtGH310M+GEE5qppprKTD/99Gbqqac2I4888oAA/7ffflN4gfuLL74wv/zyi8I10kgjKcxTTDGFwjzttNOaUUYZWCQfENCwQ/3tt9821157rXnooYf0+euvv24R0vXyqKOOasYff3wDIRdYYAGz3nrrmWWXXVbfuTK9vL///vvm9ttvN3fffbd5/fXXDTD/+OOPbSDApMAMw84777xmjTXWMKuuuqqZeOKJ28oNs384H2BYpnfeeccecMABVjqVcwpa16BBg6yMICsEbF38nywzxhhj2BVWWMEK49i///67Z2jIKLdHHnmknXXWWS1wOphiYBYmtosvvrgdOnSoFenWM5h9DRlfRrffi1hXIsw999xKQIiX7OzYZ4g/7rjj2iFDhti33nqr22DbW2+91S655JLKnLQdC6cr55gY5t1www3tiy++2HWYQw0MEwaA84855hg73njjaeenR7ojVuzdjcCll17aPv/88yF8S+f9+eef9uyzz7Yyn1cCs5Mc8803n73//vtLw9Xphz1nAAh58MEHW0Rh2VGfxRhuZM0111z2hRde6JQubd8jrU455RTLqC0z6rPgde+or6+vz951111tbfbqn54zwHHHHWdFo6+08x0xuUPQRRZZxIqCVhkNr7rqKp1mqu58Bzf1zjbbbPbZZ5+tDObYinrKAHfccYedZJJJojufUY2UcFfsVAFBd9ppJ/vzzz/H0sFb7rnnnrNIldjO7wTmlVde2cpKwgtLNzJ6xgCypreDBw+OIqQT57Jm1pEny6iW+I3pCBgG/eKSSy7piGZ//PGH3XTTTQvBzKgeZ5xxLDCPOeaYyrwxMDtpcPzxx9v//vuvI7iLfDyIwgJg19P5559vZFTCcEY6KLM98gR5M8EEE5jVV19d1/gzzDCDGk++//5789JLLxnRws3LL7+s9QijZNbDS4xHCy20kLntttvM5JNP7i0XysAusc0226gxKq+tscce2yy//PJmlVVWMTPPPLMarjAIvfrqq+bOO+80jz/+uOKWV48sLdW2MNNMM4VAqy4PBuh2+vbbb+2KK66YO5IYuSyxmCrEupYJlljb7GGHHdZaQbiRk75TF7qGMF5mPXkvWamwTBNKB5d65M8zzzxqixArZma1SL+TTjrJihUzWJ+TfCicvUo9mQLEUqYiEQTTHeX+h5Bi2YtS3v755x/tWDENB/UJ6lx//fVLGVxE2rSWfA7G9J36Meq88sorUf11yy23WBnZQSagToxbDJpepJ4wwEEHHRREmtE6yyyzWBGXhXCmXpjKx1gQU8SxxdpYNF1wwQVBmKkbm8B9991XqGok0mijjeaFGVwmnXRS+8QTTxSqt2xh/yQqGFaRZLQaMc7kVrX99tsb0bZzyyUL8I2IX51bk+/dsxCz5Vhy72LvTz75ZLAoeswmm2xilltuuWC5dKZMK6rboOtkJer97rvvjNgysrIrf9d1BsA58uWXX3oBF841E000kTp2vIU8GSiIYv3z5BpVNv/66y8jEsBbJisD757YEbKy9B0wi6Zv1lxzzcIeSRRcFEU62pdgjlD7vu/KvO86A+DehaC+BLKM/LKa+vzzz29EpPqq1/d46YoksR+YH374wfsJDICLd8455/SWCWWIoSrIONQvOkCoisryus4AYvo1XKEkc15pPzmuVkQ9RPMln7j1lQdepi7fKKUtcUDlMp6v/skmm8xbt/uG9nuRus4A+PC5QgkJUbSTXH2///57sPMp5+tIV0f6TtBG3jcwCbaGMikkEV19ee27cp3eu84AzJViEfPCCaJvvPGGQeyWSXzLPB8iGPNukQTMYkn0MhZtff7556pgFqnXlcU4lMfwRWF2dRe9d50BUPDE/u+FC/H98ccfG3GJesv4Mr755hvz6KOPejuK76gfy1yRhHifbrrpvJ9QJ8rtPffc4y3jy4BZsQyGGID6CXvrReo6AyD+85QlRKn42oOrhSxiXHPNNUY8aNrJWfnUO+WUUwY7M+s7RvjCCy+cldV6R92XX365Sq/Wy4gHGJ0QMp/EcvoFym1PkjTY9XT11VerYyRtSXP/C8ejwdn999/f+sypaSAffPBB9aPznasnfScPE7T4EdKf5/6PIQaHjnRUZv0OZpxFsR48LIYErYRgpj3c2Z9++mkujFUUQHx2PX3yySeKVAhxCErAxbbbbmvfe+89L0wiQi0MJTaAICGpD4Y49thjvXWFMsQYY8W5k9sGOK277rr2tdde81Yn0sKKU8pKIGuwPuClvgMPPNDyTS9STxgARHBzgpzrmPRo5X83qmTK0I5jlGPG/eCDD9TeLiJf3bPEAIaYyRES87JEG5em4+mnnx402yZhnnHGGTXSCdMwbUpYuzLFjTfeaMViafFbxMAsS0T7zDPPlIa56Ic9YwCxbFkXAJrV+cl3EApRiE18jjnmUG8bI36sscbKZSLXKYScHX300UXp0VZelMxcke3gBmYuCfe2s88+u5UQcPVDOGYNMb6rA5x32203i5TrVeoZA4DQhRde2AqScEj77k4aOMI6pvCVT76nrMTfW1mqdUzHm266yYqV0qsLJNvlOQlvUZhxK7/77rsdw1ykgp4yABE2u+yyS9QoThM29n+I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");
        sdMetadata.put("longDescription", "Kafka Service");
        sdMetadata.put("providerDisplayName", "Pivotal");
        sdMetadata.put("documentationUrl", "https://docs.pivotal.io");
        sdMetadata.put("supportUrl", "https://docs.pivotal.io");
        sdMetadata.put("costs", getCosts());
        return sdMetadata;
    }

    private Map<String,Object> getPlanMetadata() {
        Map<String, Object> planMetadata = new HashMap<>();
        planMetadata.put("costs", getCosts());
        planMetadata.put("bullets", getBullets());


        return planMetadata;
    }

    private List<Map<String,Object>> getCosts()
    {
        Map<String,Object> costsMap = new HashMap<>();

        Map<String,Object> amount = new HashMap<>();
        amount.put("usd", 0.0);

        costsMap.put("amount", amount);
        costsMap.put("unit", "MONTHLY");

        List<Map<String,Object>> costMapList = Collections.singletonList(costsMap);
        return costMapList;
    }

    private List<String> getBullets() {
        return Arrays.asList("dev plan",
                "Shared Storage",
                "Unlimited connections");
    }

}
