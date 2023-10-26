--
-- PostgreSQL database dump
--

-- Dumped from database version 14.9 (Homebrew)
-- Dumped by pg_dump version 14.9 (Homebrew)

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

-- -- Create Database
-- CREATE DATABASE stc_database;
--
-- -- Connect to the newly created database
-- \c stc_database;

CREATE ROLE test WITH LOGIN SUPERUSER PASSWORD 'password';
--
-- Name: user_table; Type: TABLE; Schema: public; Owner: test
--


CREATE TABLE public.user_detail
(
    id       SERIAL PRIMARY KEY,
    name     character varying(255) NOT NULL,
    email    character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    address  text
);

ALTER TABLE public.user_detail
    OWNER TO test;

--
-- Name: user_table_id_seq; Type: SEQUENCE; Schema: public; Owner: test
--

-- CREATE SEQUENCE public.user_table_id_seq
--     AS integer
--     START WITH 1
--     INCREMENT BY 1
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
--
-- ALTER TABLE public.user_table_id_seq OWNER TO test;
--
-- --
-- -- Name: user_table_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: test
-- --
--
-- ALTER SEQUENCE public.user_table_id_seq OWNED BY public.user_table.id;
--
--
-- --
-- -- Name: user_table id; Type: DEFAULT; Schema: public; Owner: test
-- --
--
-- ALTER TABLE ONLY public.user_table ALTER COLUMN id SET DEFAULT nextval('public.user_table_id_seq'::regclass);
--
--
-- --
-- -- Data for Name: user_table; Type: TABLE DATA; Schema: public; Owner: test
-- --
--
-- --
-- -- Name: user_table_id_seq; Type: SEQUENCE SET; Schema: public; Owner: test
-- --
--
-- SELECT pg_catalog.setval('public.user_table_id_seq', 1, true);
--
--
-- --
-- -- Name: user_table user_table_pkey; Type: CONSTRAINT; Schema: public; Owner: test
-- --
--
-- ALTER TABLE ONLY public.user_table
--     ADD CONSTRAINT user_table_pkey PRIMARY KEY (id);


CREATE TABLE public.donations
(
    id             SERIAL PRIMARY KEY,
    amount         character varying(255) NOT NULL,
    donation_date  DATE                   NOT NULL,
    user_detail_id INT                    NOT NULL,
    FOREIGN KEY (user_detail_id) REFERENCES public.user_detail (id)
        ON DELETE CASCADE
);


ALTER TABLE public.donations
    OWNER TO test;

-- --
-- -- Name: donations_id_seq; Type: SEQUENCE; Schema: public; Owner: test
-- --
--
-- CREATE SEQUENCE public.donations_id_seq
--     AS integer
--     START WITH 1
--     INCREMENT BY 1
--     NO MINVALUE
--     NO MAXVALUE
--     CACHE 1;
--
--
-- ALTER TABLE public.donations_id_seq OWNER TO test;
--
-- --
-- -- Name: donations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: test
-- --
--
-- ALTER SEQUENCE public.donations_id_seq OWNED BY public.user_table.id;
--
--
-- --
-- -- Name: donations id; Type: DEFAULT; Schema: public; Owner: test
-- --
--
-- ALTER TABLE ONLY public.donations ALTER COLUMN id SET DEFAULT nextval('public.donations_id_seq'::regclass);
--
--
-- --
-- -- Data for Name:donations; Type: TABLE DATA; Schema: public; Owner: test
-- --
--
-- --
-- -- Name: donations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: test
-- --
--
-- SELECT pg_catalog.setval('public.donations_id_seq', 1, true);
--
--
-- --
-- -- Name: donations donations_pkey; Type: CONSTRAINT; Schema: public; Owner: test
-- --
--
-- ALTER TABLE ONLY public.donations
--     ADD CONSTRAINT donations_pkey PRIMARY KEY (id);
--


--
-- PostgreSQL database dump complete
--

