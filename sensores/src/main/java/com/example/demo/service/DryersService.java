package com.example.demo.service;

import com.example.demo.model.DryersLog;
import com.example.demo.repository.DryersLogRepository;
import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
@RequiredArgsConstructor
public class DryersService {

    private JacksonObjectMapper jacksonObjectMapper = new JacksonObjectMapper();
    private final DryersLogRepository dryersLogRepository;

    @SneakyThrows
    private UnirestInstance getInstance(){

        Unirest.config().reset();
        UnirestInstance unirest = Unirest.primaryInstance();
        unirest.config()
                .verifySsl(false)
                .setObjectMapper(jacksonObjectMapper)
                .connectTimeout(5000)
                .concurrency(200, 2);
        return unirest;
    }

    public void buscarDados(@RequestBody String url){
        HttpResponse<String> retorno = getInstance().get(url).asString();
        DryersLog dryersLog = new Gson().fromJson(retorno.getBody(),DryersLog.class);
        dryersLog.setId(dryersLog.getChannel().getId());
        dryersLog.getChannel().setDryersLog(dryersLog);
        dryersLog.getFeeds().forEach(f-> f.setDryersLog(dryersLog));
        dryersLogRepository.save(dryersLog);
        log.info("Salvou");
    }

}
