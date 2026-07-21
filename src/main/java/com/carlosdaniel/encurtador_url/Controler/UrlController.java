package com.carlosdaniel.encurtador_url.Controler;

import com.carlosdaniel.encurtador_url.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class UrlController {

    @Autowired
    private UrlService urlService;

    // 1. Endpoint para encurtar a URL (Recebe o link gigante)
    @PostMapping("/api/encurtar")
    public ResponseEntity<String> encurtar(@RequestBody Map<String, String> request) {
        String urlOriginal = request.get("urlOriginal");

        if (urlOriginal == null || urlOriginal.isEmpty()) {
            return ResponseEntity.badRequest().body("A URL original não pode ser vazia!");
        }

        String codigoCurto = urlService.encurtarUrl(urlOriginal);

        // Retorna o link curto pronto para o usuário copiar e colar
        String linkPronto = "http://localhost:8080/" + codigoCurto;
        return ResponseEntity.ok(linkPronto);
    }

    // 2. Endpoint de Redirecionamento (A Mágica do Bit.ly)
    @GetMapping("/{codigoCurto}")
    public ResponseEntity<Void> redirecionar(@PathVariable String codigoCurto) {
        // Vai no nosso serviço (que consulta o Redis primeiro, e depois o PostgreSQL)
        String urlOriginal = urlService.buscarUrlOriginal(codigoCurto);

        if (urlOriginal != null) {
            // Monta o cabeçalho HTTP instruindo o navegador a redirecionar (Status 302 Found)
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(urlOriginal));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        // Se o código não existir, retorna Erro 404 (Not Found)
        return ResponseEntity.notFound().build();
    }
}