package br.gov.sp.fatec.entrepreneur.service;

import br.gov.sp.fatec.entrepreneur.domain.Entrepreneur;
import br.gov.sp.fatec.entrepreneur.repository.EntrepreneurRepository;
import br.gov.sp.fatec.project.domain.Project;
import br.gov.sp.fatec.utils.commons.SendEmail;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static br.gov.sp.fatec.utils.exception.InactiveException.throwIfEntrepreneurIsInactive;
import static br.gov.sp.fatec.utils.exception.NotFoundException.throwIfEntrepreneurIsNull;

@Service
public class EntrepreneurService {

    @Autowired
    private EntrepreneurRepository repository;

    @Autowired
    private SendEmail sendEmail;

    public Entrepreneur save(Entrepreneur entrepreneur) {
        entrepreneur.setActive(false);
        entrepreneur.setPassword(Base64.getEncoder().encodeToString(entrepreneur.getPassword().getBytes()));
        sendEmail.sendMail(entrepreneur.getEmail(), "entrepreneur");

        return repository.save(entrepreneur);
    }

    public List<Entrepreneur> findAll() {
        return repository.findAll();
    }

    public List<Entrepreneur> findActive() {
        return repository.findAllByActive(true);
    }

    public Entrepreneur findById(Long id) {
        Entrepreneur found = repository.getOne(id);
        throwIfEntrepreneurIsNull(found, id);

        return found;
    }

    public void activate(String b64) {
        JSONObject jsonObject = new JSONObject(new String(Base64.getDecoder().decode(b64)));
        Entrepreneur found = repository.findByEmail(jsonObject.get("email").toString());
        throwIfEntrepreneurIsNull(found);

        found.setActive(true);
        repository.save(found);
    }

    public Entrepreneur update(Long id, Entrepreneur entrepreneur) {
        Entrepreneur found = repository.findById(id).orElse(null);
        throwIfEntrepreneurIsNull(found, id);

        found.setName(entrepreneur.getName());
        found.setEmail(entrepreneur.getEmail());
        found.setCompany(entrepreneur.getCompany());
        found.setTelephone(entrepreneur.getTelephone());
        found.setCpf(entrepreneur.getCpf());
        found.setActive(entrepreneur.isActive());

        return repository.save(found);
    }

    public Entrepreneur login(Map<String, String> login) {
        String password = login.get("password");
        String email = login.get("email");

        password =  Base64.getEncoder().encodeToString(password.getBytes());
        Entrepreneur entrepreneur = repository.findByEmailAndPassword(email, password);

        throwIfEntrepreneurIsNull(entrepreneur);
        throwIfEntrepreneurIsInactive(entrepreneur);

        return entrepreneur;
    }
}
