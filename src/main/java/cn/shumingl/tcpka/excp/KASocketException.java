package cn.shumingl.tcpka.excp;

public class KASocketException extends RuntimeException {
    private String module;
    private String code;
    private String type;
    private String message;

    public KASocketException(String type, String module, String code, String message) {
        this(type, module, code, message, null);
    }

    public KASocketException(String type, String module, String code, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.code = code;
        this.module = module;
        this.message = message;
    }

    public String toString() {
        return String.format("[%s.%s.%s]%s", type, module, code, message);
    }

}
