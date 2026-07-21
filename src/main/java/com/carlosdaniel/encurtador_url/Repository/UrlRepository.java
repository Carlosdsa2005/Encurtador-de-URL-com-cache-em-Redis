package com.carlosdaniel.encurtador_url.Repository;

import com.carlosdaniel.encurtador_url.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    // O Spring cria o SQL automaticamente (SELECT * FROM urls WHERE codigo_curto = ?)
    Url findByCodigoCurto(String codigoCurto);
}