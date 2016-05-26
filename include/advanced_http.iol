type HttpRequest: void {
    .url: string
    .method?: string
    .followRedirects?: bool
    .requestBody?: string
    .auth?: void {
        .username: string
        .password: string
    }
    .headers[0,*]: void {
        .field: string
        .value: string
    }
}

type HttpResponse: void {
    .code: int
    .body: string
    .headers[0,*]: void {
        .field: string
        .value[1,*]: string
    }
}

interface AdvancedHttpIface {
    RequestResponse:
        execute(HttpRequest)(HttpResponse)
}

outputPort AdvancedHttp {
    Interfaces: AdvancedHttpIface
}

embedded {
    Java:
        "dk.danthrane.jolie.http.AdvancedHttpService" in AdvancedHttp
}