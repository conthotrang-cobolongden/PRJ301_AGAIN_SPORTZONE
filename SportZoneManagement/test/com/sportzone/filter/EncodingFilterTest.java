package com.sportzone.filter;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/** Kiem tra EncodingFilter dat UTF-8 truoc khi chuyen request. */
public final class EncodingFilterTest {
    private EncodingFilterTest() { }

    public static void main(String[] args) throws Exception {
        AtomicBoolean requestEncoded = new AtomicBoolean();
        AtomicBoolean responseEncoded = new AtomicBoolean();
        AtomicBoolean chained = new AtomicBoolean();
        ServletRequest request = proxy(ServletRequest.class, "setCharacterEncoding", requestEncoded);
        ServletResponse response = proxy(ServletResponse.class, "setCharacterEncoding", responseEncoded);
        FilterChain chain = (req, res) -> chained.set(true);

        new EncodingFilter().doFilter(request, response, chain);
        require(requestEncoded.get() && responseEncoded.get() && chained.get(),
                "Filter must encode request/response and continue chain");
        System.out.println("EncodingFilterTest PASSED");
    }

    private static <T> T proxy(Class<T> type, String observedMethod, AtomicBoolean observed) {
        return type.cast(Proxy.newProxyInstance(EncodingFilterTest.class.getClassLoader(),
                new Class<?>[]{type}, (proxy, method, args) -> {
                    if (observedMethod.equals(method.getName()) && "UTF-8".equals(args[0])) observed.set(true);
                    return null;
                }));
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
