package org.easyj.spring.orm.controller.jpa;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.easyj.orm.EntityService;
import org.easyj.spring.view.EasyView;
import org.easyj.validation.sequences.POSTSequence;
import org.easyj.validation.sequences.PUTSequence;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class JPAPersistentEntityController extends JPAEntityController {

    public <T> ModelAndView post(HttpServletResponse response,
            final T entity, BindingResult result) {
        logger.debug("Receiving POST Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(response, entity, result, POSTSequence.class);
    }

    public <T> ModelAndView put(HttpServletResponse response,
            final T entity, BindingResult result) {
        logger.debug("Receiving PUT Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(response, entity, result, PUTSequence.class);
    }

    private <T> ModelAndView save(HttpServletResponse response, final T entity, BindingResult result, Class validatorSequence) {

        ModelAndView mav = createModelAndView();
        T retEntity = null;
        int httpResponseStatus = HttpServletResponse.SC_BAD_REQUEST;
        String retStatus = EntityService.STATUS_ERROR;

        if(entity == null) {
            result.addError(new ObjectError(result.getObjectName(), EntityService.ENTITY_NULL + "." + result.getTarget().getClass().getSimpleName()));
            logger.debug("ERROR: Cannot save null entity");
        } else {
            bindValidatorErrors(validator.validate(entity, validatorSequence), result);
            if(!result.hasErrors()) {
                httpResponseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                retStatus = getService().save(entity);
                if(EntityService.STATUS_SUCCESS.equals(retStatus)) {
                    httpResponseStatus = HttpServletResponse.SC_OK;
                    retEntity = (T) getService().loadUK(entity);
                    logger.debug("Entity saved: entity[" + entity + "]");
                } else {
                    if(!EntityService.STATUS_ERROR.equals(retStatus)) {
                        httpResponseStatus = HttpServletResponse.SC_CONFLICT;
                    }
                    result.addError(new ObjectError(result.getObjectName(), retStatus));
                    logger.debug("Entity not saved: return error[" + retStatus + "]");
                }
            }
        }

        configModelAndView(mav, retStatus, retEntity, result);

        response.setStatus(httpResponseStatus);
        return mav;
    }

    public <T> ModelAndView delete(HttpServletResponse response, Class<T> klazz, Map<String, Object> params) {
        logger.debug("Receiving DELETE Request for: " + klazz.getSimpleName() + ": " + params);
        ModelAndView mav = createModelAndView();
        BindingResult result = createBindingResult(klazz);
        String retStatus = EntityService.STATUS_ERROR;
        int httpResponseStatus = HttpServletResponse.SC_BAD_REQUEST;

        if(params.isEmpty()) {
            result.addError(new ObjectError(result.getObjectName(), EntityService.NO_PARAMS_SET + "." + result.getTarget().getClass().getSimpleName()));
            logger.debug("ERROR: Cannot delete entity without params ");
        } else {
            getService().remove(klazz, params);
            retStatus = EntityService.STATUS_SUCCESS;
            httpResponseStatus = HttpServletResponse.SC_OK;
            logger.debug("Entity deleted successfully ");
        }

        mav.addObject(EasyView.STATUS, retStatus);
        mav.addObject(EasyView.BINDING_RESULT, result);
        mav.setViewName(result.getObjectName());

        response.setStatus(httpResponseStatus);
        return mav;
    }

}
