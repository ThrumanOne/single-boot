package com.ratel.single.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.UUID;

import com.ratel.single.pojo.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class BusinessException extends RuntimeException {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessException.class);
    private Object[] params;
    private int exCode;
    private String exDesc;
    private String exStack;

    public BusinessException(int exCode, String exDesc) {
        super(String.valueOf(exCode));
        this.setExCode(exCode);
        this.setExDesc(this.getExDescByCode(exCode, exDesc));
    }

    public BusinessException(int exCode, String exDesc, Throwable e) {
        super(String.valueOf(exCode), e);
        this.setExCode(exCode);
        this.setExDesc(this.getExDescByCode(exCode, exDesc));
    }

    public BusinessException(int exCode, String exDesc, Object... params) {
        super(String.valueOf(exCode));
        this.params = params;
        this.setExCode(exCode);
        this.setExDesc(this.getExDescByCode(exCode, exDesc));
    }

    public BusinessException(int exCode, String exDesc, Throwable e, Object... params) {
        super(String.valueOf(exCode), e);
        this.params = params;
        this.setExCode(exCode);
        this.setExDesc(this.getExDescByCode(exCode, exDesc));
    }

    public BusinessException(int exCode) {
        super(String.valueOf(exCode));
        this.setExCode(exCode);
        this.setExDesc(this.getExDescByCode(exCode, (String)null));
    }

    public BusinessException(int exCode, Object... params) {
        super(String.valueOf(exCode));
        this.setExCode(exCode);
        this.params = params;
        this.setExDesc(this.getExDescByCode(exCode, (String)null));
    }

    public BusinessException(int exCode, Throwable e, Object... params) {
        super(String.valueOf(exCode), e);
        this.setExCode(exCode);
        this.params = params;
        this.setExDesc(this.getExDescByCode(exCode, (String)null));
        this.setExStack(this.getStackTraceMessage(e));
    }

    public BusinessException(int exCode, Throwable e) {
        super(String.valueOf(exCode), e);
        this.setExCode(exCode);
        this.setExDesc(this.getExDescByCode(exCode, (String)null));
        this.exStack = this.getStackTraceMessage(e);
    }

    private String getExDescByCode(int exCode, String message) {
        String code = String.valueOf(exCode);
        if (ExceptionCode.EX_MAP != null && ExceptionCode.EX_MAP.get(code) != null) {
            String exDesc = null;
            if (ExceptionCode.EX_MAP != null) {
                exDesc = ExceptionCode.EX_MAP.get(code);
            } else {
                exDesc = String.valueOf(exCode);
            }

            String uuid = MDC.get("uuid");
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
            }

            if (ResultCodeEnum.SYSTEM_EXCEPTION.getCode() == exCode) {
                exDesc = "[" + uuid + "]" + exDesc;
            }

            if (exDesc == null) {
                exDesc = ExceptionCode.EX_MAP.get(String.valueOf(ResultCodeEnum.SYSTEM_EXCEPTION.getCode()));
                exDesc = "[" + uuid + "]" + exDesc;
            }

            if (message != null) {
                if (this.params != null && this.params.length > 0) {
                    Object[] oldParams = this.params;
                    this.params = new Object[this.params.length + 1];
                    this.params[0] = message;

                    for(int i = 0; i < oldParams.length; ++i) {
                        this.params[i + 1] = oldParams[i];
                    }
                } else {
                    this.params = new Object[]{message};
                }
            }

            return exDesc;
        } else {
            return message == null ? String.valueOf(exCode) : message;
        }
    }

    public String getExStack() {
        return this.exStack;
    }

    public void setExStack(String exStack) {
        this.exStack = exStack;
    }

    public int getExCode() {
        return this.exCode;
    }

    public void setExCode(int exCode) {
        this.exCode = exCode;
    }

    public String getExDesc() {
        return this.exDesc;
    }

    public void setExDesc(String exDesc) {
        if (this.params != null) {
            this.exDesc = MessageFormat.format(exDesc, this.params);
        } else {
            this.exDesc = exDesc;
        }

    }

    @Override
    public String getMessage() {
        String _exCode = super.getMessage();
        if (_exCode == null) {
            _exCode = String.valueOf(this.exCode);
        }

        return "[" + _exCode + "]" + this.exDesc;
    }

    private String getStackTraceMessage(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;

        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException var10) {
                    this.LOG.error(var10.getMessage(), var10);
                }
            }

            if (pw != null) {
                pw.close();
            }

        }

        return sw.toString();
    }
}