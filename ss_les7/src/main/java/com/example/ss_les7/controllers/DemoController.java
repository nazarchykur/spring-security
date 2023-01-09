package com.example.ss_les7.controllers;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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