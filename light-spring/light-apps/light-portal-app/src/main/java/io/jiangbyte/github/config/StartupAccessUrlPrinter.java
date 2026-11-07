package io.jiangbyte.github.config;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 应用启动完成后打印访问地址：本地、局域网，以及常见 API 文档入口。
 */
@Slf4j
@NoArgsConstructor
public final class StartupAccessUrlPrinter {

    /**
     * 根据 Environment 解析端口/上下文/网卡 IP，并输出启动访问信息。
     */
    public static void print(Environment env) {
        // 1. 读取应用名、端口、context-path；本地固定 127.0.0.1，局域网只取 192.*
        String appName = env.getProperty("spring.application.name",
                env.getProperty("spring.config.name", "application"));
        String port = resolvePort(env);
        String contextPath = resolveContextPath(env);
        String localIp = "127.0.0.1";
        List<String> lanIps = resolveLanIpv4List();

        // 2. 组装并打印本地 / 局域网 / API Doc 地址
        printAccessUrls(appName, port, contextPath, localIp, lanIps);
    }

    /**
     * 解析对外服务端口：优先用容器启动后写入的 local.server.port（含随机端口），再回退 server.port。
     */
    private static String resolvePort(Environment env) {
        String port = env.getProperty("local.server.port");
        if (!StringUtils.hasText(port)) {
            port = env.getProperty("server.port", "8080");
        }
        return port;
    }

    /**
     * 解析 Servlet 上下文路径，统一成以 / 开头、无尾部 / 的形式；根路径返回空串。
     */
    private static String resolveContextPath(Environment env) {
        String contextPath = env.getProperty("server.servlet.context-path",
                env.getProperty("server.servlet.contextPath", ""));
        if (!StringUtils.hasText(contextPath) || "/".equals(contextPath)) {
            return "";
        }
        if (!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        return contextPath;
    }

    /**
     * 枚举网卡，只收集 192.* 局域网 IPv4（本地 127.* 单独打印，此处不重复）。
     */
    private static List<String> resolveLanIpv4List() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // 1. 跳过未启用或回环网卡
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // 2. 只要 IPv4，且仅保留 192 网段
                    if (!(address instanceof Inet4Address) || address.isLoopbackAddress()) {
                        continue;
                    }
                    String hostAddress = address.getHostAddress();
                    if (!isAllowedPrintIp(hostAddress) || hostAddress.startsWith("127.")) {
                        continue;
                    }
                    if (!ipList.contains(hostAddress)) {
                        ipList.add(hostAddress);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析局域网 IP 失败，将仅打印本地地址", e);
        }
        // 3. 枚举失败时兜底：仅当本机地址也是 192.* 才补一条
        if (ipList.isEmpty()) {
            try {
                String hostAddress = InetAddress.getLocalHost().getHostAddress();
                if (isAllowedPrintIp(hostAddress) && hostAddress.startsWith("192.")) {
                    ipList.add(hostAddress);
                }
            } catch (Exception ignored) {
                // ignore
            }
        }
        return ipList;
    }

    /**
     * 启动日志只打印 127.* / 192.*，其它网段一律忽略。
     */
    private static boolean isAllowedPrintIp(String ip) {
        return StringUtils.hasText(ip) && (ip.startsWith("127.") || ip.startsWith("192."));
    }

    /** 标签列宽，保证 URL 左对齐。 */
    private static final int LABEL_WIDTH = 10;

    /**
     * 按固定格式输出本地、局域网与 API 文档访问地址。
     */
    private static void printAccessUrls(String appName, String port, String contextPath,
                                        String localIp, List<String> lanIps) {
        // 1. 组装本地 / 局域网 / Doc 地址列表
        String localBase = "http://" + localIp + ":" + port + contextPath;
        List<String> lanBases = new ArrayList<>();
        if (lanIps.isEmpty()) {
            lanBases.add(localBase);
        } else {
            for (String lanIp : lanIps) {
                lanBases.add("http://" + lanIp + ":" + port + contextPath);
            }
        }
        String primaryLanBase = lanBases.get(0);
        List<String> docBases = List.of(
                primaryLanBase + "/doc.html",
                primaryLanBase + "/swagger-ui.html",
                primaryLanBase + "/swagger-ui/index.html",
                primaryLanBase + "/v3/api-docs"
        );

        // 2. 固定空格对齐输出（不用 Tab，避免终端错位）
        StringBuilder message = new StringBuilder();
        message.append('\n')
                .append("----------------------------------------------------------\n")
                .append("  应用 '").append(appName).append("' 启动成功！\n")
                .append('\n');
        appendLabeledUrls(message, "本地", List.of(localBase));
        appendLabeledUrls(message, "局域网", lanBases);
        appendLabeledUrls(message, "API Doc", docBases);
        message.append("----------------------------------------------------------");
        log.info(message.toString());
    }

    /**
     * 追加一组带标签的 URL：首行带标签，后续行缩进对齐。
     */
    private static void appendLabeledUrls(StringBuilder message, String label, List<String> urls) {
        String paddedLabel = padLabel(label);
        for (int i = 0; i < urls.size(); i++) {
            if (i == 0) {
                message.append("  ").append(paddedLabel).append(urls.get(i)).append('\n');
            } else {
                message.append("  ").append(" ".repeat(LABEL_WIDTH)).append(urls.get(i)).append('\n');
            }
        }
    }

    /**
     * 标签右补空格到固定宽度，末尾带冒号。
     */
    private static String padLabel(String label) {
        String withColon = label + ":";
        if (withColon.length() >= LABEL_WIDTH) {
            return withColon + " ";
        }
        return withColon + " ".repeat(LABEL_WIDTH - withColon.length());
    }

}
