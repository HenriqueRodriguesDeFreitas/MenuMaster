package com.api.menumaster.service;

import com.api.menumaster.repository.EntradaIngredienteItemRepository;
import com.api.menumaster.repository.EntradaIngredienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class EntradaIngrediente {


    private final EntradaIngredienteRepository entradaIngredienteRepository;
    private final EntradaIngredienteItemRepository entradaIngredienteItemRepository;

    public EntradaIngrediente(EntradaIngredienteRepository entradaIngredienteRepository, EntradaIngredienteItemRepository entradaIngredienteItemRepository) {
        this.entradaIngredienteRepository = entradaIngredienteRepository;
        this.entradaIngredienteItemRepository = entradaIngredienteItemRepository;
    }

    @Test
    public void salvarEntrada(){

    }
}
