zpi-komunikator
===============

### Build serwera ###
Build status: [![Build Status](https://magnum.travis-ci.com/bkowalik/zpi-komunikator.svg?token=EDiXM5sspWmHpkhCTcdr&branch=master)](https://magnum.travis-ci.com/bkowalik/zpi-komunikator)

Build odpala się automatycznie po commitcie na travisie, paczka potem jest deployowana an ec2.
Linki do serwera:
[https://ec2-54-77-232-158.eu-west-1.compute.amazonaws.com/](https://ec2-54-77-232-158.eu-west-1.compute.amazonaws.com/) 
lub po adresie IP:
[https://54.77.232.158](https://54.77.232.158/)

LUB na HTTP na porcie 9000. Póki co nie działa połączenie websocket po HTTPS więc zaleca się korzystanie z HTTP. 

### Ręczne budowanie serwera ###
1. Pobranie Javy (najlepiej od Oracle) w wersji 6,7 lub 8.
2. Pobranie SBT (Scala Build Tool) ze strony [http://www.scala-sbt.org/](http://www.scala-sbt.org/) 
3. Po sklonowaniu repo należy wejść do głównego katalogu i uruchomić SBT. Nastąpi pobranie SBT wymaganego przez projekt (tylko za pierwszym razem).
4. Pojawi się znak zachęty w postaci `[zpi-server] $` i należy wpisać polecenie `compile`.
5. Aby uruchomić w wersji deweloperskiej należy wpisać polecenie `run`
6. Aby stworzyć paczkę uruchomieniową należy wpisać polecenie `dist`. Paczka zostanie utworzona w katalogu ***target/universal/*** o nazwie **zpi-server-VERSION.zip**
7. Aby uruchomić serwer z paczki należy rozpakować archiwum i z katalogu ***bin*** uruchomić `zpi-server`(Windows) lub `zpi-server.sh` (Linux).