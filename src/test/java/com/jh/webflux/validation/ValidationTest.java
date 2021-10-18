package com.jh.webflux.validation;

import com.jh.webflux.validator.Book;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationTest {
    @Test
    void test_jsr380_notnull_validation(){
//        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Validator validator=createValidator();
        Book book=new Book(null,"name","subName");
        Set<ConstraintViolation<Book>> validate = validator.validate(book);
        for(ConstraintViolation<Book> constraintViolation:validate){
            System.out.println(constraintViolation.getMessage());
        }

        assertEquals(1,validate.size());
        assertEquals("id not null",validate.iterator().next().getMessage());
    }

    @Test
    void test_jsr380_notblank_validation(){ //공백도 안되는.. 셋중 제일 강한 유효성체크..
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Book book=new Book("id"," ","subName");
        Set<ConstraintViolation<Book>> validate = validator.validate(book);
        for(ConstraintViolation<Book> constraintViolation:validate){
            System.out.println(constraintViolation.getMessage());
        }

        assertEquals(1,validate.size());
        assertEquals("name is not blank",validate.iterator().next().getMessage());
    }

    @Test
    void test_jsr380_notempty_validation(){
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Book book=new Book("id","name","");
        Set<ConstraintViolation<Book>> validate = validator.validate(book);
        for(ConstraintViolation<Book> constraintViolation:validate){
            System.out.println(constraintViolation.getMessage());
        }

        assertEquals(1,validate.size());
        assertEquals("subName is not empty",validate.iterator().next().getMessage());
    }

    @Test
    void test_jsr380_notempty_space_validation(){
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Book book=new Book("id","name"," ");
        Set<ConstraintViolation<Book>> validate = validator.validate(book);
        for(ConstraintViolation<Book> constraintViolation:validate){
            System.out.println(constraintViolation.getMessage());
        }

        assertEquals(0,validate.size());
//        assertEquals("subName is not empty",validate.iterator().next().getMessage());
    }

    @Test
    void test_jsr380_multiple_validation_notnull_notempty(){
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Book book=new Book(null,"name","");
        Set<ConstraintViolation<Book>> validate = validator.validate(book);
        assertEquals(2,validate.size());
        for(ConstraintViolation<Book> constraintViolation:validate){
            String fieldName=constraintViolation.getPropertyPath().toString();
            String errorMessage=constraintViolation.getMessage();
            if(fieldName.equals("id")){
                assertEquals("id not null",errorMessage);
            }else if (fieldName.equals("subName")){
                assertEquals("subName is not empty",errorMessage);
            }
        }
    }

    private Validator createValidator() {
        //return Validation.buildDefaultValidatorFactory().getValidator();
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean(); //spring에서 사용하도록 만든것..
        localValidatorFactoryBean.afterPropertiesSet(); //초기화하는거.. 반드시필요
        return localValidatorFactoryBean;
    }
}
