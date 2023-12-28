# Finalmente Parte Chata Terminada

## Recomendado baixar o arquivo em zip para melhor funcionamento

## Tabela do Banco de Dados:

```sql
create database brainly_matematica;
use brainly_matematica;

create table matter(
    id bigint(20) primary key auto_increment not null,
    name varchar(350)
);

create table formula(
    id bigint(20) primary key auto_increment not null,
    name varchar(350),
    descFormula longtext,

    matterId bigint(20),
    foreign key(matterId) references matter(id)
);


insert into matter (name) values ('Matematica');
insert into matter (name) values ('Física');
insert into formula (name, descformula, matterid) values ('Bhaskara', 'x = (-b ± √Δ)', 1);
insert into formula (name, descformula, matterid) values ('Delta', 'Δ = b^2 - 4ac', 2);
SELECT * FROM formula;
select * from matter;
