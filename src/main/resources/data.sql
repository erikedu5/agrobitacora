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

CREATE TABLE public.engineer_producers (
    id int8 NOT NULL,
    engineer_id int8 NOT NULL,
    producer_id int8 NOT NULL,
    CONSTRAINT engineer_producers_pkey PRIMARY KEY (id),
    CONSTRAINT fk_engineer FOREIGN KEY (engineer_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_producer FOREIGN KEY (producer_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT uk_engineer_producer UNIQUE (engineer_id, producer_id)
);
CREATE TABLE public.engineer_reviews (
    id int8 NOT NULL,
    engineer_id int8 NOT NULL,
    producer_id int8 NOT NULL,
    rating int4 NULL,
    review varchar(1000) NULL,
    CONSTRAINT engineer_reviews_pkey PRIMARY KEY (id),
    CONSTRAINT fk_review_engineer FOREIGN KEY (engineer_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_producer FOREIGN KEY (producer_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT uk_review UNIQUE (engineer_id, producer_id)
);

