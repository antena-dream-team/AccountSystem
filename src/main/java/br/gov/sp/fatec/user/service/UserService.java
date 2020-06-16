package br.gov.sp.fatec.user.service;

import br.gov.sp.fatec.security.domain.Authorization;
import br.gov.sp.fatec.security.repository.AuthorizationRepository;
import br.gov.sp.fatec.user.domain.User;
import br.gov.sp.fatec.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationRepository authorizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void setUsuarioRepo(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setAutorizacaoRepo(AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public User incluirUsuario(String login, String password, String authorizationName) {
        Authorization authorization = authorizationRepository.findByName(authorizationName);
        // Se nao existe
        if(authorization == null) {
            // Cria nova
            authorization = new Authorization();
            authorization.setName(authorizationName);
            authorizationRepository.save(authorization);
        }
        User user = new User();
        user.setEmail(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setAuthorizations(new ArrayList<>());
        user.getAuthorizations().add(authorization);
        userRepository.save(user);
        return user;
    }

    public List<User> buscar(String login) {
        return userRepository.findByNameContainsIgnoreCase(login);
    }

    @PreAuthorize("isAuthenticated()")
    public User buscar(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @PreAuthorize("isAuthenticated()")
    public List<User> todos() {
        List<User> retorno = new ArrayList<User>();
        for(User user: userRepository.findAll()) {
            retorno.add(user);
        }
        return retorno;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public User salvar(User user) {
        List<Authorization> authorizations = user.getAuthorizations();

        if(!authorizations.isEmpty()) {
            for(Authorization aut: authorizations) {
                // Se nao existe, cria
                if(aut.getId() == null && authorizationRepository.findByName(aut.getName()) == null) {
                    authorizationRepository.save(aut);
                }
            }
        }
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }
}
