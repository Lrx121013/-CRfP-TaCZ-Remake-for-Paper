-- TRfP 通用工具
-- 作者: Lrxmcstudio.工作室的Lrx

trfp = trfp or {}
trfp.log = function(msg)
    -- 服务端日志由 Java 端拦截；这里仅占位
end

-- 触发事件
trfp.call = function(hook, ...)
    local args = {...}
    if _G[hook] then
        _G[hook](unpack(args))
    end
end

return trfp
