package dk.danthrane.jolie.http;

import jolie.runtime.FaultException;
import jolie.runtime.JavaService;
import jolie.runtime.Value;

public class AdvancedHttpService extends JavaService {
    private final AdvancedHttp instance = new AdvancedHttp();

    public Value execute(Value request) throws FaultException {
        try {
            return instance.execute(request);
        } catch (Exception e) {
            throw new FaultException(e.getMessage());
        }
    }
}
