package com.twine.arca_app;

import com.twine.arca_app.general.Utilidades;

import org.androidannotations.rest.spring.annotations.Field;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.androidannotations.rest.spring.api.RestClientHeaders;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * Created by TWINE-DELL on 22/3/2017.
 */
@Rest(rootUrl = Utilidades.BASE_URL,
        converters = {FormHttpMessageConverter.class, StringHttpMessageConverter.class, GsonHttpMessageConverter.class})
public interface RestClient extends RestClientErrorHandling, RestClientHeaders {
    @Post("/arca/createUserAuth/")
    String createUserAuth(@Field String username,
                          @Field String nombre,
                          @Field String apellido,
                          @Field String age,
                          @Field String gender,
                          @Field String email,
                          @Field String telefono
    );

    @Get("/arca/get_comercios/")
    String get_comercios();

    @Post("/arca/save_cupon/")
    String save_cupon(@Field String descuento,
                      @Field String id_empleado,
                      @Field String codigo_usuario,
                      @Field String codigo,
                      @Field String creado);

    @Get("/arca/get_cupones/?username={username}")
    String get_cupones(@Path String username);

    @Get("/arca/get_facturas/?username={username}")
    String get_facturas(@Path String username);

    @Post("/agregar_registro/")
    String agregar_registro(@Field String tag,
                            @Field String mensaje,
                            @Field String fecha,
                            @Field String usuario);
}