package com.example.ss_les7.controllers;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class DemoController {

    // PreAuthorize
    @GetMapping("/demo1")
    @PreAuthorize("hasAuthority('read')") // hasAuthority() hasAnyAuthority() hasRole() hasAnyRole()
    public String demo() {
        return "demo1";
    }

    @GetMapping("/demo2")
    @PreAuthorize("hasAnyAuthority('read', 'write')")
    public String demo2() {
        return "demo2";
    }

    @GetMapping("/demo3/{smth}")
    @PreAuthorize("#something == authentication.name") // authentication from SecurityContext
    public String demo3(@PathVariable("smth") String something) {
        return "demo3";
    }
    
    /*
        можна використовувати складнішу логіку, але код стає менш читабельним
        і чим більше ліній коду, тим краще винести цю складну логіку до класу 
        і потім передати як у прикладі demo5:
                  @GetMapping("/demo5")
                  @PreAuthorize("@demoForConditionEvaluator.condition()") // доступ до біна через @
    
     */
    @GetMapping("/demo4/{smth}")
    @PreAuthorize(
        """
        (#something == authentication.name) or
        hasAnyAuthority("write", "read")
        """) 
    public String demo4(@PathVariable("smth") String something) {
        return "demo4";
    }

    @GetMapping("/demo5")
    @PreAuthorize("@demoForConditionEvaluator.condition()") // доступ до біна через @
    public String demo5() {
        return "demo5";
    }

    // PostAuthorize

    @GetMapping("/demo6")
    /*
        в основному використовуємо тоді, коли хочемо обмежити доступ до результату, 
        який повернеться після виконання цього метода
        
        ВАЖЛИВО: НЕ використовувати цю @PostAuthorize() з логікою, які змінюють дані в БД,
            оскільки сам метод виконається у першу чергу, і тільки опісля виконається перевірка
            на доступ до результату виконання цього метода
     */
    @PostAuthorize("returnObject == 'demo6' ") 
    public String demo6() {
        return "demo6";
    }
    
    
//    @PreFilter()  - parameter type must be either a Collection(List / Set) or Array
    
    /*
        даний приклад показує як використати @PreFilter, але суть його застосування завжди у контексті безпеки
        тобто, наприклад, відфільтрувати деякий список об'єктів, який належить тільки цьому юзеру
        
        тобто ми НЕ використовуємо @PreFilter(), щоб просто відфільтрувати 
     */

    @GetMapping("/demo7")
    @PreFilter("filterObject.contains('a')")
    public String demo7(@RequestBody List<String> values) {
        /*
            curl --location --request GET 'http://localhost:9007/demo7' \
            --header 'Authorization: Basic YmlsbDpwYXNz' \
            --header 'Content-Type: application/json' \
            --data-raw '["qwer", "ty", "asd", "zxc", "qaaaz"]'
            
            
            тобто ми бачимо, що запит відбувався але з переданого списку 
                ["qwer", "ty", "asd", "zxc", "qaaaz"]
            було відфільтровано рядки які містять букву "а"
            і тому на виході отримали:
                values = [asd, qaaaz]
                
                
         */
        System.out.println("values = " + values); 
        
        return "demo7";
    }
    
    
    //  @PostFilter  - returned type must be either a Collection(List / Set) or Array

    @GetMapping("/demo8")
    @PostFilter("filterObject.contains('a')")
    public List<String> demo7() {
//        return List.of("a", "b", "c"); // List.of creates an immutable collection => DO NOT work
//        return Arrays.asList("a", "b", "c");
        
        var list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        
        return list; // поверне тільки список, де буде літера "a"
    }
    
}

/*
    @PostAuthorize
    
    Ще раз нагадування:
        Використовуючи анотацію @PostAuthorize, дуже важливо мати на увазі, що ця анотація дозволить спочатку виконати 
        бізнес-логіку методу, а лише потім буде відпрацює логіка у @PostAuthorize. 
        Отже, будьте обережні та не використовуйте цю анотацію з методами, які виконують модифікаційні запити, 
        наприклад, видалення користувача або оновлення користувача.
        
        
        Одним із хороших варіантів використання анотації @PostAuthorize буде метод, який читає деяку інформацію 
        з бази даних та інших джерел і повертає якесь значення. 
        Наприклад, ми можемо використовувати анотацію @PostAuthorize з методом, який читає дані користувача з бази даних, 
        а потім повертає об’єкт користувача з методу.
        
        
        
        Об'єкт returnObject:
        
            Анотація @PostAuthorize має доступ до об’єкта, який повертає метод. До будь-якого об’єкта, який збирається 
            повернути ваш метод, можна отримати доступ у виразі через “ returnObject “.
            
                        @PostAuthorize("returnObject.userId == principal.userId)
            
            
            @PreAuthorize дозволить методу повертати значення, лише якщо користувач, який увійшов у систему, має роль 
            АДМІНІСТРАТОРА або є власником об’єкта, який повертається.
            
                        @PostAuthorize( "hasRole('ADMIN') або returnObject.userId == principal.userId" )
 */

/*
    ось ще кілька прикладів використання @PreAuthorize @PostAuthorize:
    
            @PreAuthorize ("hasRole('ROLE_WRITE')")
            public void addBook(Book book);
        
            @PostAuthorize ("returnObject.owner == authentication.name")
            public Book getBook();
        
            @PreAuthorize ("#book.owner == authentication.name")
            public void deleteBook(Book book);
            
            
            
            
            @PreAuthorize("hasRole('ROLE_VIEWER') or hasRole('ROLE_EDITOR')")
            public boolean isValidUsername3(String username)
            
            @PreAuthorize("#username == authentication.principal.username")
            public String getMyRoles(String username)
            
            @PostAuthorize("returnObject.username == authentication.principal.nickName")
            public CustomUser loadUserDetail(String username) {
                return userRoleRepository.loadUserByUserName(username);
            }
 */

/*
@PreFilter and @PostFilter

    @PreFilter and @PostFilter are designated to use with Spring security to be able to filter collections or 
    arrays based on the authorization.
    
    To have this working, you need to use expression-based access control in spring security
    
            @PreFilter - filters the collection or arrays before executing method.
            @PostFilter - filters the returned collection or arrays after executing the method.
    
    
    So, let's say your getUser() returns List of Users. Spring Security will iterate through the list and remove 
    any elements for which the applied expression is false (e.g. is not admin, and does not have read permission)
    
    filterObject is built-in object on which filter operation is performed and you can apply various conditions to 
    this object (basically all built-in expressions are available here, e.g. principal, authentication), 
    for example you can do
    
            @PostFilter ("filterObject.owner == authentication.name")
    
    Though those filters are useful, it is really inefficient with large data sets, and basically you lose control 
    over your result, instead Spring controls the result.
    
    ==================================================================================================================
    
            @PreAuthorize ("hasRole('ROLE_READ')")
            @PostFilter ("filterObject.owner == authentication.name")
            public List<Book> getBooks();
            
            @PreFilter("filterObject.owner == authentication.name")
            public void addBook(List<Book> books);
    
    The filterObject is built-in object on which filter operation is performed. In the above code, for the first 
    method getBooks(), we have used @PreAuthorize and @PostFilter annotations. Before executing method, user is 
    authorized on the basis of role and then after executing, the returned object is filtered on the basis of owner. 
    Second method addBook() is only using @PreFilter on the basis of owner.
    

 */

/*

    через SpEl ми маємо доступ до параметрів і можемо брати цю змінну для перевірки  
    authentication - це об'єкт який зберігається у SecurityContext 
 */


/*
     @PreAuthorize("hasAuthority('read')") - означає, що юзер може викликати цей ендпоінт тільки тоді якщо має права 'read'
 */

/*
    hasAuthority() hasAnyAuthority() hasRole() hasAnyRole()
    
    використовуючи Аспект ми можемо використовувати це і у сервісах, і репозиторіях, але доцільно це робити
    саме у контролері, який якраз перший у цій цепочці приймає запит 
 */