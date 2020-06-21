package br.gov.sp.fatec.cadi.service;

import br.gov.sp.fatec.cadi.domain.Cadi;
import br.gov.sp.fatec.cadi.repository.CadiRepository;
import br.gov.sp.fatec.utils.commons.Md5;
import br.gov.sp.fatec.utils.commons.SendEmail;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static br.gov.sp.fatec.utils.commons.Md5.md5;
import static br.gov.sp.fatec.utils.exception.InactiveException.throwIfCadiIsInactive;
import static br.gov.sp.fatec.utils.exception.NotFoundException.throwIfCadiIsNull;

@Service
@Transactional
public class CadiService {

    @Autowired
    private CadiRepository repository;

    @Autowired
    private SendEmail sendEmail;

    public Cadi save(Cadi cadi, String url) {
        cadi.setActive(false);
        cadi.setPassword(md5(cadi.getPassword()));
        sendEmail.sendMail(cadi.getEmail(), url);
        return repository.save(cadi);
    }

    public Cadi save(Cadi cadi) {
//        cadi.setActive(false);
//        cadi.setPassword(md5(cadi.getPassword()));
        return repository.save(cadi);
    }

    public List<Cadi> findAll() {
        return repository.findAll();
    }

    public List<Cadi> findActive() {
        return repository.findAllByActive(true);
    }

    public Cadi findById(Long id) {
        Cadi found = repository.findById(id).orElse(null);
        throwIfCadiIsNull(found, id);
        return found;
    }

    public Cadi update(Long id, Cadi cadi) {
        Cadi found = repository.findById(id).orElseGet(null);
        throwIfCadiIsNull(found, id);

       // found.setActive(cadi.isActive());
        try{
            found.setActive(cadi.isActive());
            found.setName(cadi.getName());
            found.setEmail(cadi.getEmail());
            found.setCpf(cadi.getCpf());
            found.setPosition(cadi.getPosition());

        }catch(Exception e){
            e.printStackTrace();

        }
 //       found.setName(cadi.getName());
 //       found.setEmail(cadi.getEmail());
 //       found.setCpf(cadi.getCpf());
 //       found.setPosition(cadi.getPosition());

        return repository.save(found);
    }

    public Cadi activate(String b64) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new String(Base64.getDecoder().decode(b64)));
            Cadi found = repository.findByEmail(jsonObject.get("email").toString());
            throwIfCadiIsNull(found);

            found.setActive(true);
            return repository.save(found);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Cadi login(Map<String, String> login) {
        String password = login.get("password");
        String email = login.get("email");

        password =  Base64.getEncoder().encodeToString(password.getBytes());
        Cadi cadi = repository.findByEmailAndPassword(email, password);

        throwIfCadiIsNull(cadi);
        throwIfCadiIsInactive(cadi);

        return cadi;
    }
}
