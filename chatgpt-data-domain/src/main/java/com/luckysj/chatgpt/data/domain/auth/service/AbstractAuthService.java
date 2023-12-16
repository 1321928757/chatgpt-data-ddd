package com.luckysj.chatgpt.data.domain.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.cache.Cache;
import com.luckysj.chatgpt.data.domain.auth.model.entity.AuthStateEntity;
import com.luckysj.chatgpt.data.domain.auth.model.valobj.AuthTypeVo;
import com.luckysj.chatgpt.data.domain.auth.repository.IAuthRepository;
import com.luckysj.chatgpt.data.types.exception.ChatGPTException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 鉴权服务模板模式抽象类
 * @create 2023/12/05 15:24:49
 */
@Slf4j
public abstract class AbstractAuthService implements IAuthService{
    /** 默认的Base64编码的密钥SecretKey 要替换为你自己的，并且最好是通过配置的方式使用 */
    private static final String defaultBase64EncodedSecretKey = "A*D^D%wq";
    /**Base64编码的密钥*/
    private final String base64EncodedSecretKey = Base64.encodeBase64String(defaultBase64EncodedSecretKey.getBytes());
    private final Algorithm algorithm = Algorithm.HMAC256(Base64.decodeBase64(Base64.encodeBase64String(defaultBase64EncodedSecretKey.getBytes())));

    @Resource
    private Cache<String, String> codeCache;

    @Resource
    private IAuthRepository iAuthRepository;

    @Override
    public AuthStateEntity doLogin(String code) {
        // 1. 初步格式校验，验证码应为4位的数字
        if(!code.matches("\\d{4}")){
            log.info("鉴权失败，验证码格式错误：{}", code);
            return AuthStateEntity.builder()
                    .code(AuthTypeVo.CODE_INVALIDATION.getCode())
                    .info(AuthTypeVo.CODE_INVALIDATION.getInfo())
                    .build();
        }

        // 2. 校验判断，非成功则直接返回，使用模板模式，具体校验交付子类实现
        AuthStateEntity authStateEntity = this.checkCode(code);
        if(!authStateEntity.getCode().equals(AuthTypeVo.CODE_SUCCESS.getCode())){
            return authStateEntity;
        }


        // 3. 如果用户账号不存在，则添加用户账号
        String openId = authStateEntity.getOpenId();
        boolean addResult = iAuthRepository.insertUserIfNotExist(openId);
        if(!addResult){
            log.error("插入用户账号新数据时出错，openid:{}", openId);
            throw new ChatGPTException("插入用户账号新数据时出错，openid:" + openId);
        }

        // 4. 获取 Token 并返回
        Map<String, Object> chaim = new HashMap<>();
        chaim.put("openId", authStateEntity.getOpenId());
        String token = encode(openId, 7 * 24 * 60 * 60 * 1000, chaim);
        authStateEntity.setToken(token);

        return authStateEntity;
    }

    protected abstract AuthStateEntity checkCode(String code);

    /**
     * 这里就是产生jwt字符串的地方
     * jwt字符串包括三个部分
     * 1. header
     * -当前字符串的类型，一般都是“JWT”
     * -哪种算法加密，“HS256”或者其他的加密算法
     * 所以一般都是固定的，没有什么变化
     * 2. payload
     * 一般有四个最常见的标准字段（下面有）
     * iat：签发时间，也就是这个jwt什么时候生成的
     * jti：JWT的唯一标识
     * iss：签发人，一般都是username或者userId
     * exp：过期时间
     */
    protected String encode(String issuer, long ttlMillis, Map<String, Object> claims) {
        // iss签发人，ttlMillis生存时间，claims是指还想要在jwt中存储的一些非隐私信息
        if (claims == null) {
            claims = new HashMap<>();
        }
        long nowMillis = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                // 荷载部分
                .setClaims(claims)
                // 这个是JWT的唯一标识，一般设置成唯一的，这个方法可以生成唯一标识
                .setId(UUID.randomUUID().toString())//2.
                // 签发时间
                .setIssuedAt(new Date(nowMillis))
                // 签发人，也就是JWT是给谁的（逻辑上一般都是username或者userId）
                .setSubject(issuer)
                .signWith(SignatureAlgorithm.HS256, base64EncodedSecretKey);//这个地方是生成jwt使用的算法和秘钥
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);// 4. 过期时间，这个也是使用毫秒生成的，使用当前时间+前面传入的持续时间生成
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    // 相当于encode的反向，传入jwtToken生成对应的username和password等字段。Claim就是一个map
    // 也就是拿到荷载部分所有的键值对
    protected Claims decode(String jwtToken) {
        // 得到 DefaultJwtParser
        return Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(base64EncodedSecretKey)
                // 设置需要解析的 jwt
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    // 判断jwtToken是否合法
    protected boolean isVerify(String jwtToken) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(jwtToken);
            // 校验不通过会抛出异常
            // 判断合法的标准：1. 头部和荷载部分没有篡改过。2. 没有过期
            return true;
        } catch (Exception e) {
            log.error("jwt isVerify Err", e);
            return false;
        }
    }

    @Override
    public AuthStateEntity getAuthTest() {
        // 生成虚拟身份信息
        String openid = "xfg";
        String code = "2133";
        codeCache.put(code, openid);
        codeCache.put(openid, code);
        AuthStateEntity authStateEntity = AuthStateEntity.builder()
                .code(AuthTypeVo.CODE_SUCCESS.getCode())
                .info(AuthTypeVo.CODE_SUCCESS.getInfo())
                .openId(openid)
                .build();

        Map<String, Object> chaim = new HashMap<>();
        chaim.put("openId", authStateEntity.getOpenId());
        String token = encode(authStateEntity.getOpenId(), 7 * 24 * 60 * 60 * 1000, chaim);
        authStateEntity.setToken(token);
        return authStateEntity;
    }

    @Override
    public String getAuthCode(String openid) {
        // 缓存验证码
        String isExistCode = codeCache.getIfPresent(openid);

        // 判断验证码 - 不考虑验证码重复问题
        if (StringUtils.isBlank(isExistCode)) {
            // 创建验证码
            String code = RandomStringUtils.randomNumeric(4);
            codeCache.put(code, openid);
            codeCache.put(openid, code);
            isExistCode = code;
        }
        return isExistCode;
    }
}
