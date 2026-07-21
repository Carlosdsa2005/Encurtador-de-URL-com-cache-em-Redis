package com.carlosdaniel.encurtador_url;

import com.carlosdaniel.encurtador_url.Repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class UrlService {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int TAMANHO_HASH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private UrlRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate; // Nossa ferramenta para falar com o Redis

    // 1. Gera o código curto
    private String gerarCodigoCurto() {
        StringBuilder hash = new StringBuilder(TAMANHO_HASH);
        for (int i = 0; i < TAMANHO_HASH; i++) {
            hash.append(BASE62.charAt(RANDOM.nextInt(BASE62.length())));
        }
        return hash.toString();
    }

    // 2. Salva a URL no Postgres e no Cache (Redis)
    public String encurtarUrl(String urlOriginal) {
        String codigo = gerarCodigoCurto();

        // Salva no banco de dados relacional (PostgreSQL)
        Url novaUrl = new Url(urlOriginal, codigo);
        repository.save(novaUrl);

        // Salva no Redis (Cache) com duração de 24 horas
        redisTemplate.opsForValue().set(codigo, urlOriginal, Duration.ofHours(24));

        return codigo;
    }

    // 3. Busca a URL original com estratégia de Cache-First
    public String buscarUrlOriginal(String codigoCurto) {
        // Tenta buscar do Redis primeiro (Resposta em milissegundos!)
        String urlCached = redisTemplate.opsForValue().get(codigoCurto);

        if (urlCached != null) {
            System.out.println("🔥 Cache HIT! Pegou do Redis: " + codigoCurto);
            return urlCached;
        }

        // Se não achou no Redis (Cache Miss), busca no PostgreSQL
        System.out.println("🐌 Cache MISS! Buscando no PostgreSQL: " + codigoCurto);
        Url url = repository.findByCodigoCurto(codigoCurto);

        if (url != null) {
            // Como achou no Postgres, salva no Redis de novo para o próximo acesso
            redisTemplate.opsForValue().set(codigoCurto, url.getUrlOriginal(), Duration.ofHours(24));
            return url.getUrlOriginal();
        }

        // Retorna nulo se o código não existir em lugar nenhum
        return null;
    }
}
