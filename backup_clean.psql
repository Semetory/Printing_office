--
-- PostgreSQL database dump
--

\restrict dN1VrM6YRfL8Tw0R1grNEktPEZwLruJVhQb3iTiiQklp4FBDVz6mChK9UUgCw4f

-- Dumped from database version 15.18
-- Dumped by pg_dump version 15.18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: login_attempts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.login_attempts (
    username character varying(255) NOT NULL,
    attempts integer NOT NULL,
    lock_time timestamp(6) without time zone
);


ALTER TABLE public.login_attempts OWNER TO postgres;

--
-- Name: order_files; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_files (
    order_id bigint NOT NULL,
    filename character varying(255)
);


ALTER TABLE public.order_files OWNER TO postgres;

--
-- Name: order_files_storage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_files_storage (
    id bigint NOT NULL,
    data bytea NOT NULL,
    file_name character varying(255) NOT NULL,
    file_type character varying(255) NOT NULL,
    order_id bigint NOT NULL
);


ALTER TABLE public.order_files_storage OWNER TO postgres;

--
-- Name: order_files_storage_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.order_files_storage_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.order_files_storage_id_seq OWNER TO postgres;

--
-- Name: order_files_storage_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.order_files_storage_id_seq OWNED BY public.order_files_storage.id;


--
-- Name: order_services; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_services (
    order_id bigint NOT NULL,
    service_key character varying(255)
);


ALTER TABLE public.order_services OWNER TO postgres;

--
-- Name: orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orders (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    email character varying(255) NOT NULL,
    format character varying(255) NOT NULL,
    fullname character varying(255) NOT NULL,
    order_number character varying(255) NOT NULL,
    paper character varying(255) NOT NULL,
    payment character varying(255),
    phone character varying(255) NOT NULL,
    quantity integer NOT NULL,
    status character varying(255),
    total integer NOT NULL
);


ALTER TABLE public.orders OWNER TO postgres;

--
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orders_id_seq OWNER TO postgres;

--
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;


--
-- Name: price_configs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.price_configs (
    item_key character varying(255) NOT NULL,
    item_name character varying(255) NOT NULL,
    price integer NOT NULL
);


ALTER TABLE public.price_configs OWNER TO postgres;

--
-- Name: order_files_storage id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_files_storage ALTER COLUMN id SET DEFAULT nextval('public.order_files_storage_id_seq'::regclass);


--
-- Name: orders id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);


--
-- Data for Name: login_attempts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.login_attempts (username, attempts, lock_time) FROM stdin;
admin	0	\N
\.


--
-- Data for Name: order_files; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_files (order_id, filename) FROM stdin;
\.


--
-- Data for Name: order_files_storage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_files_storage (id, data, file_name, file_type, order_id) FROM stdin;
\.


--
-- Data for Name: order_services; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.order_services (order_id, service_key) FROM stdin;
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.orders (id, created_at, email, format, fullname, order_number, paper, payment, phone, quantity, status, total) FROM stdin;
\.


--
-- Data for Name: price_configs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.price_configs (item_key, item_name, price) FROM stdin;
A0	Формат А0 (за шт.)	150
A1	Формат А1 (за шт.)	90
A2	Формат А2 (за шт.)	45
A3	Формат А3 (за шт.)	20
A4	Формат А4 (за шт.)	10
A5	Формат А5 (за шт.)	7
A6	Формат А6 (за шт.)	4
coated	Мелованная бумага (за шт.)	4
matte	Матовая бумага (за шт.)	7
glossy	Глянцевая бумага (за шт.)	5
cardboard	Картон (за шт.)	15
design	Дизайнерский картон (за шт.)	25
sticky	Самоклеящаяся бумага (за шт.)	12
lamination	Ламинация (за шт.)	15
folding	Фальцовка (за шт.)	3
creasing	Биговка (за шт.)	4
gluing	Склейка (за шт.)	8
urgent	Срочный заказ (фикс. наценка)	200
\.


--
-- Name: order_files_storage_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.order_files_storage_id_seq', 1, false);


--
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.orders_id_seq', 1, false);


--
-- Name: login_attempts login_attempts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.login_attempts
    ADD CONSTRAINT login_attempts_pkey PRIMARY KEY (username);


--
-- Name: order_files_storage order_files_storage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_files_storage
    ADD CONSTRAINT order_files_storage_pkey PRIMARY KEY (id);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- Name: price_configs price_configs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.price_configs
    ADD CONSTRAINT price_configs_pkey PRIMARY KEY (item_key);


--
-- Name: orders uk_nthkiu7pgmnqnu86i2jyoe2v7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT uk_nthkiu7pgmnqnu86i2jyoe2v7 UNIQUE (order_number);


--
-- Name: order_services fks0is53vplrljgctgbtssr32h7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_services
    ADD CONSTRAINT fks0is53vplrljgctgbtssr32h7 FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: order_files fks0kadxgnbahuj84y4o4e6s53g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_files
    ADD CONSTRAINT fks0kadxgnbahuj84y4o4e6s53g FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- PostgreSQL database dump complete
--

\unrestrict dN1VrM6YRfL8Tw0R1grNEktPEZwLruJVhQb3iTiiQklp4FBDVz6mChK9UUgCw4f

