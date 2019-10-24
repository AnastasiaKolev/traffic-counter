# Traffic counter

Приложение для захвата траффика с ip адреса по выбору при вводе номера канала NIF [_i_]

Пример списка доступных адресов захвата траффика:

NIF[0]: en0
      
      : link layer address: *
      
      : address: /*
      
      : address: /*

NIF[1]: p2p0
      
      : link layer address: *

NIF[2]: awdl0
      
      : link layer address: *
      
      : address: /*

NIF[3]: utun0
      
      : address: /*

NIF[4]: lo0

      : address: /127.0.0.1
      
      : address: /0:0:0:0:0:0:0:1
      
      : address: /fe80:0:0:0:0:0:0:1

NIF[5]: gif0

NIF[6]: stf0

NIF[7]: XHC20

Запуск приложения: 
1. _name_:target _user-name_$ sudo java -jar traffic-counter.jar
2. Далее ввод пароля
3. И выбор канала, например NIF [_0_], вводим 0
4. Выход из приложения Ctrl+C
