# Учебный проект mySql
***

![Build Status](https://travis-ci.com/zvozdin/mySql.svg?branch=master)
***

### **Техническое задание**

Приложение на языке Java, реализующее функционал консольного клиента   
для работы с конкретной базой данных. 

   *Требования*:
* приложение должно использовать паттерн MVC 
* приложение должно иметь консольный интерфейс для    
   взаимодействия с пользователем.  
* должны быть реализованы следующие консольные команды:  
  * **сonnect**  
    * команда для подключения к соответствующей БД:  
      * формат команды: connect|database|username|password, где  
            - database - имя БД,    
            - username -  имя пользователя БД,    
            - password - пароль пользователя БД.  
    * формат вывода: текстовое сообщение с результатом выполнения операции 
    
  * **tables**  
    * команда выводит список всех таблиц  
      * формат команды: tables (без параметров)
      * формат вывода: [table1, table2, table3]  
      
  * **clear**
    * команда очищает содержимое указанной таблицы  
    * формат команды: clear|tableName, где    
          - tableName - имя очищаемой таблицы  
    * формат вывода: текстовое сообщение с результатом выполнения операции  
    
  * **drop**
    * команда удаляет заданную таблицу
    * формат команды: drop|tableName, где  
          - tableName - имя удаляемой таблицы  
    * формат вывода: текстовое сообщение с результатом выполнения операции
    
  * **create**
    * команда создает новую таблицу с заданными полями
    * формат команды: create|tableName|column1|column2| ... |columnN, где  
          - tableName - имя таблицы,  
          - column1 - имя первого столбца,     
          - column2 - имя второго столбца,    
          - columnN - имя n-го столбца;  
    * формат вывода: текстовое сообщение с результатом выполнения операции
    
  * **find**  
    * команда для получения содержимого указанной таблицы    
    * формат команды: find|tableName, где    
          - tableName - имя таблицы     
    * формат вывода: табличка в консольном формате  
    
             +---------+---------+---------+  
             | column1 | column2 | columnN |  
             +---------+---------+---------+  
             | value1  | value2  | valueN  |  
             +---------+---------+---------+  
             
  * **insert**
    * команда для вставки одной строки в заданную таблицу  
    * формат команды:  
   insert|tableName|column1|value1|column2|value2| ... |columnN|valueN, где       
          - tableName - имя таблицы,  
          - column1 - имя первого столбца  
          - value1 - значение первого столбца  
          - column2 - имя второго столбца  
          - value2 - значение второго столбца  
          - columnN - имя n-го столбца  
          - valueN - значение n-го столбца  
    * формат вывода: текстовое сообщение с результатом выполнения операции  
    
  * **update**
    * команда обновит запись, установив значение column1 = value1,   
   для которой соблюдается условие column2 = value2  
    * формат команды: update|tableName|column1|value1|column2|value2, где  
          - tableName - имя таблицы  
          - column1 - имя обновляемого столбца  
          - value1 - значение обновляемого столбца  
          - column2 -  имя столбца записи которое проверяется  
          - value2 - значение, которому должен соответствовать столбец column2 для обновляемой записи  
    * формат вывода: табличный, как при find без удаленных записей   
    
  * **delete**
    * команда удаляет одну или несколько записей для которых соблюдается условие column = value  
    * формат команды: delete|tableName|column|value, где  
          - tableName - имя таблицы  
          - сolumn - имя столбца записи, которая удаляеться  
          - value - значение, которому должен соответствовать столбец column1 для удаляемой записи  
    * формат вывода: табличный, как при find со старыми значениями удаляемых записей.
    
  * **help**
    * команда выводит в консоль список всех доступных команд
    * формат команды: help (без параметров)
    * формат вывода: текст описания команд
    
  * **exit**
    * команда для отключения от БД и выход из приложения
    * формат команды exit (без параметров)
    * формат вывода: текстовое сообщение с результатом выполнения операции
    
***!!! Для выполнения всех тестов необходимо ввести имя своей базы данных, имя юзера и пароль в   
src/main/resources/db_connect.properties***
