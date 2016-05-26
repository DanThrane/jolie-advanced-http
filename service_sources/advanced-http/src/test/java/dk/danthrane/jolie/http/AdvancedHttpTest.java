package dk.danthrane.jolie.http;

import jolie.runtime.Value;
import jolie.runtime.ValueVector;
import org.junit.Test;

public class AdvancedHttpTest {
    @Test
    public void testGoogle() throws Exception {
        AdvancedHttp http = new AdvancedHttp();

        Value request = Value.create();
        request.setFirstChild("url", "https://api.github.com/users/" + "DanThrane" + "/repos");
        request.setFirstChild("method", "GET");
        ValueVector headers = ValueVector.create();
        Value header = Value.create();
        header.setFirstChild("field", "User-Agent");
        header.setFirstChild("value", "JPM");
        headers.add(header);
        request.children().put("header", headers);

        Value auth = Value.create();
        auth.setFirstChild("username", "DanThrane");
        auth.setFirstChild("password", "hyf7l7w38Wp5wmras9");
        request.setFirstChild("auth", auth);
        /*
        execute@AdvancedHttp({
            .url = "https://api.github.com/users/" + request.username +
                "/repos",
            .method = "GET",
            .headers[0].field = "User-Agent",
            .headers[0].value = "JPM"
        })(httpResponse);
         */
        Value execute = http.execute(request);
        System.out.println(execute.getFirstChild("body").strValue());
    }
}
