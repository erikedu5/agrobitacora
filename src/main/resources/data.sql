-- public.roles definition

-- Drop table

-- DROP TABLE public.roles;

CREATE TABLE public.roles (
	id int8 NOT NULL,
	active bool NULL,
	is_default bool NULL,
	"name" varchar(255) NULL,
	CONSTRAINT roles_pkey PRIMARY KEY (id),
	CONSTRAINT ukofx66keruapi6vyqpv6f2or37 UNIQUE (name)
);

-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
	id int8 NOT NULL,
	active bool NULL,
	"name" varchar(255) NULL,
	"password" varchar(255) NULL,
	user_name varchar(255) NULL,
	role_id int8 NOT NULL,
	CONSTRAINT ukk8d0f2n7n88w1a16yhua64onx UNIQUE (user_name),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);


-- public.users foreign keys

ALTER TABLE public.users ADD CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(id) ON DELETE CASCADE;

CREATE TYPE public.irrigation_type AS ENUM (
    'ASPERSION',
    'GOTEO',
    'GRAVEDAD',
    'HIDROPONICO'
);