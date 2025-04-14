--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2
-- Dumped by pg_dump version 17.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
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
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    role_id integer NOT NULL,
    role_name character varying(20),
    CONSTRAINT roles_role_name_check CHECK (((role_name)::text = ANY ((ARRAY['ROLE_USER'::character varying, 'ROLE_ADMIN'::character varying])::text[])))
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- Name: roles_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.roles ALTER COLUMN role_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.roles_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id bigint NOT NULL,
    account_expiry_date date,
    account_non_expired boolean NOT NULL,
    account_non_locked boolean NOT NULL,
    created_date timestamp(6) without time zone,
    credentials_expiry_date date,
    credentials_non_expired boolean NOT NULL,
    email character varying(50) NOT NULL,
    enabled boolean NOT NULL,
    is_two_factor_enabled boolean NOT NULL,
    password character varying(120),
    sign_up_method character varying(255),
    two_factor_secret character varying(255),
    updated_date timestamp(6) without time zone,
    username character varying(20) NOT NULL,
    role_id integer
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.users ALTER COLUMN user_id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.roles (role_id, role_name) FROM stdin;
1	ROLE_USER
2	ROLE_ADMIN
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, account_expiry_date, account_non_expired, account_non_locked, created_date, credentials_expiry_date, credentials_non_expired, email, enabled, is_two_factor_enabled, password, sign_up_method, two_factor_secret, updated_date, username, role_id) FROM stdin;
1	2026-04-02	t	f	2025-04-02 23:20:06.444214	2026-04-02	t	user1@example.com	t	f	$2a$10$G0isueb7GzJzjeBu2F3jGeQu00lLOWhoKSKTL6sc0vW4qzAkQl/Re	email	\N	2025-04-02 23:20:06.444214	user1	1
2	2026-04-02	t	t	2025-04-02 23:20:06.584802	2026-04-02	t	admin@example.com	t	f	$2a$10$cKz158NxW7n.NRXHkvMG1uMrVJsCnobeALd6E8yCS0i7y1yEdMmQ2	email	\N	2025-04-02 23:20:06.584802	admin	2
3	\N	t	t	2025-04-14 11:06:13.700064	\N	t	romeo24@romeo.com	t	f	$2a$10$E9DAW3XJYtzKJ6Bpn/O/QeQCZBY9cidin2dgTUWaKWBnO4oqcLMze	\N	\N	2025-04-14 11:17:33.65226	romeoUser24	2
4	\N	t	t	2025-04-14 11:19:52.462533	\N	t	uniform27@uniform.org	t	f	$2a$10$p0JtAac8uexTn4U0SAJfi.jOz8HR93aQ4Kyx2sVu5wcfFFi2TZ/le	\N	\N	2025-04-14 11:31:05.616299	belcinard pachinco	1
\.


--
-- Name: roles_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.roles_role_id_seq', 2, true);


--
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 4, true);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (role_id);


--
-- Name: users uk6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: users ukr43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT ukr43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: users fkp56c1712k691lhsyewcssf40f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id) REFERENCES public.roles(role_id);


--
-- PostgreSQL database dump complete
--

