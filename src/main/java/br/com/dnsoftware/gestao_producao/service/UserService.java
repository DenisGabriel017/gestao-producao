package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.model.User;
import br.com.dnsoftware.gestao_producao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


}
