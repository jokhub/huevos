--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-06-18 12:39:20

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
-- TOC entry 226 (class 1259 OID 16816)
-- Name: clientes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.clientes (
    id integer NOT NULL,
    nombre character varying(100),
    dni character varying(20),
    apellido character varying(100),
    telefono character varying(50),
    email character varying(100),
    activo boolean DEFAULT true,
    direccion character varying(200)
);


ALTER TABLE public.clientes OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16815)
-- Name: clientes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.clientes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.clientes_id_seq OWNER TO postgres;

--
-- TOC entry 4880 (class 0 OID 0)
-- Dependencies: 225
-- Name: clientes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.clientes_id_seq OWNED BY public.clientes.id;


--
-- TOC entry 224 (class 1259 OID 16774)
-- Name: detalle_venta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.detalle_venta (
    id integer NOT NULL,
    id_venta integer,
    id_producto integer,
    categoria character varying(10) NOT NULL,
    cantidad integer NOT NULL,
    precio double precision NOT NULL,
    direccion character varying(200) NOT NULL
);


ALTER TABLE public.detalle_venta OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16773)
-- Name: detalle_venta_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.detalle_venta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.detalle_venta_id_seq OWNER TO postgres;

--
-- TOC entry 4881 (class 0 OID 0)
-- Dependencies: 223
-- Name: detalle_venta_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.detalle_venta_id_seq OWNED BY public.detalle_venta.id;


--
-- TOC entry 228 (class 1259 OID 16841)
-- Name: empleados; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.empleados (
    id_empleados integer NOT NULL,
    nombre character varying(100) NOT NULL,
    fecha_ingreso date NOT NULL,
    fecha_salida date,
    dni character varying(20) NOT NULL,
    correo character varying(100) NOT NULL,
    telefono character varying(50) NOT NULL,
    apellido character varying(50) NOT NULL,
    rol character varying(20) NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    CONSTRAINT empleados_rol_check CHECK (((rol)::text = ANY ((ARRAY['gerente'::character varying, 'administrador'::character varying, 'preventista'::character varying])::text[])))
);


ALTER TABLE public.empleados OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16840)
-- Name: empleados_id_empleados_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.empleados_id_empleados_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.empleados_id_empleados_seq OWNER TO postgres;

--
-- TOC entry 4882 (class 0 OID 0)
-- Dependencies: 227
-- Name: empleados_id_empleados_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.empleados_id_empleados_seq OWNED BY public.empleados.id_empleados;


--
-- TOC entry 232 (class 1259 OID 16874)
-- Name: inventario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventario (
    id integer NOT NULL,
    id_producto integer,
    cantidad double precision,
    fecha_actualizacion date,
    precio_unitario double precision NOT NULL
);


ALTER TABLE public.inventario OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16873)
-- Name: inventario_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.inventario_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.inventario_id_seq OWNER TO postgres;

--
-- TOC entry 4883 (class 0 OID 0)
-- Dependencies: 231
-- Name: inventario_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.inventario_id_seq OWNED BY public.inventario.id;


--
-- TOC entry 220 (class 1259 OID 16687)
-- Name: pedido; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pedido (
    id integer NOT NULL,
    id_producto integer NOT NULL,
    producto_nombre character varying(100) NOT NULL,
    tamanio character varying(20) NOT NULL,
    cantidad integer NOT NULL,
    id_cliente integer NOT NULL,
    fecha_pedido date NOT NULL,
    hora_pedido time without time zone NOT NULL,
    fecha_entrega date NOT NULL,
    hora_entrega time without time zone NOT NULL,
    estado character varying(20) NOT NULL,
    observaciones character varying(255)
);


ALTER TABLE public.pedido OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16686)
-- Name: pedido_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pedido_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pedido_id_seq OWNER TO postgres;

--
-- TOC entry 4884 (class 0 OID 0)
-- Dependencies: 219
-- Name: pedido_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pedido_id_seq OWNED BY public.pedido.id;


--
-- TOC entry 230 (class 1259 OID 16867)
-- Name: producto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.producto (
    id_producto integer NOT NULL,
    categoria character varying(30),
    precio_unitario double precision
);


ALTER TABLE public.producto OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16866)
-- Name: producto_id_producto_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.producto_id_producto_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.producto_id_producto_seq OWNER TO postgres;

--
-- TOC entry 4885 (class 0 OID 0)
-- Dependencies: 229
-- Name: producto_id_producto_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.producto_id_producto_seq OWNED BY public.producto.id_producto;


--
-- TOC entry 218 (class 1259 OID 16390)
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios (
    id integer NOT NULL,
    nombre_usuario character varying(50) NOT NULL,
    contrasena character varying(50) NOT NULL,
    rol character varying(20) NOT NULL,
    CONSTRAINT usuarios_rol_check CHECK (((rol)::text = ANY ((ARRAY['gerente'::character varying, 'administrador'::character varying])::text[])))
);


ALTER TABLE public.usuarios OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16389)
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usuarios_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuarios_id_seq OWNER TO postgres;

--
-- TOC entry 4886 (class 0 OID 0)
-- Dependencies: 217
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- TOC entry 222 (class 1259 OID 16757)
-- Name: venta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.venta (
    id integer NOT NULL,
    id_empleados integer NOT NULL,
    id_cliente integer NOT NULL,
    fecha_emicion date,
    total_venta double precision
);


ALTER TABLE public.venta OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16756)
-- Name: venta_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.venta_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.venta_id_seq OWNER TO postgres;

--
-- TOC entry 4887 (class 0 OID 0)
-- Dependencies: 221
-- Name: venta_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.venta_id_seq OWNED BY public.venta.id;


--
-- TOC entry 4680 (class 2604 OID 16819)
-- Name: clientes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientes ALTER COLUMN id SET DEFAULT nextval('public.clientes_id_seq'::regclass);


--
-- TOC entry 4679 (class 2604 OID 16777)
-- Name: detalle_venta id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detalle_venta ALTER COLUMN id SET DEFAULT nextval('public.detalle_venta_id_seq'::regclass);


--
-- TOC entry 4682 (class 2604 OID 16844)
-- Name: empleados id_empleados; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empleados ALTER COLUMN id_empleados SET DEFAULT nextval('public.empleados_id_empleados_seq'::regclass);


--
-- TOC entry 4685 (class 2604 OID 16877)
-- Name: inventario id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventario ALTER COLUMN id SET DEFAULT nextval('public.inventario_id_seq'::regclass);


--
-- TOC entry 4677 (class 2604 OID 16690)
-- Name: pedido id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedido ALTER COLUMN id SET DEFAULT nextval('public.pedido_id_seq'::regclass);


--
-- TOC entry 4684 (class 2604 OID 16870)
-- Name: producto id_producto; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto ALTER COLUMN id_producto SET DEFAULT nextval('public.producto_id_producto_seq'::regclass);


--
-- TOC entry 4676 (class 2604 OID 16393)
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- TOC entry 4678 (class 2604 OID 16760)
-- Name: venta id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.venta ALTER COLUMN id SET DEFAULT nextval('public.venta_id_seq'::regclass);


--
-- TOC entry 4868 (class 0 OID 16816)
-- Dependencies: 226
-- Data for Name: clientes; Type: TABLE DATA; Schema: public; Owner: postgres
--
INSERT INTO public.clientes (nombre, dni, apellido, telefono, email, activo, direccion) VALUES
('yawar', '11069922', 'bautista', '7755424', 'jijij@gmail.com', true, 'aqui'),
('yamil', '123456', 'torres', '764532', 'ysdys@gmail.com', true, 'jaijxia'),
('jamil', '830635', 'torres', '77665544', 'pipi@gmail.com', true, 'por ahi');

--
-- TOC entry 4866 (class 0 OID 16774)
-- Dependencies: 224
-- Data for Name: detalle_venta; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.detalle_venta (id, id_venta, id_producto, categoria, cantidad, precio, direccion) VALUES
(1, 2, 2, 'Pequeño', 100, 0.20000000298023224, 'aqui');

--
-- TOC entry 4870 (class 0 OID 16841)
-- Dependencies: 228
-- Data for Name: empleados; Type: TABLE DATA; Schema: public; Owner: postgres
--
INSERT INTO public.empleados 
(id_empleados, nombre, fecha_ingreso, fecha_salida, dni, correo, telefono, apellido, rol, activo) VALUES
(3, 'joel', '2025-03-02', NULL, '1234567', 'yoyo@gmail.com', '77646444', 'rosales', 'preventista', true),
(2, 'yawar', '2025-12-07', NULL, '11069922', 'yaw@gmail.com', '7755424', 'bautista', 'preventista', true),
(4, 'jamil', '2025-06-04', NULL, '4354242', 'qlo@gmail.com', '7656443', 'torres', 'preventista', true);

--
-- TOC entry 4874 (class 0 OID 16874)
-- Dependencies: 232
-- Data for Name: inventario; Type: TABLE DATA; Schema: public; Owner: postgres
--
INSERT INTO public.inventario (id, id_producto, cantidad, fecha_actualizacion, precio_unitario) VALUES
(1, 1, 10000, '2025-06-18', 0.5);


--
-- TOC entry 4862 (class 0 OID 16687)
-- Dependencies: 220
-- Data for Name: pedido; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.pedido 
(id, id_producto, producto_nombre, tamanio, cantidad, id_cliente, fecha_pedido, hora_pedido, fecha_entrega, hora_entrega, estado, observaciones) VALUES
(1, 1, 'Huevo', 'Pequeño', 12, 1, '2025-06-17', '15:02:00', '2025-06-17', '15:02:00', 'Rezagado', NULL);

--
-- TOC entry 4872 (class 0 OID 16867)
-- Dependencies: 230
-- Data for Name: producto; Type: TABLE DATA; Schema: public; Owner: postgres
--
INSERT INTO public.producto (id_producto, categoria, precio_unitario) VALUES
(1, 'Pequeño', 0.5);


--
-- TOC entry 4860 (class 0 OID 16390)
-- Dependencies: 218
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.usuarios (id, nombre_usuario, contrasena, rol) VALUES
(1, 'gerente1', '1234', 'gerente'),
(2, 'admin1', 'abcd', 'administrador');

--
-- TOC entry 4864 (class 0 OID 16757)
-- Dependencies: 222
-- Data for Name: venta; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.venta (id, id_empleados, id_cliente, fecha_emicion, total_venta) VALUES
(2, 2, 1, '2025-06-18', 20);

--
-- TOC entry 4888 (class 0 OID 0)
-- Dependencies: 225
-- Name: clientes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.clientes_id_seq', 3, true);


--
-- TOC entry 4889 (class 0 OID 0)
-- Dependencies: 223
-- Name: detalle_venta_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.detalle_venta_id_seq', 1, true);


--
-- TOC entry 4890 (class 0 OID 0)
-- Dependencies: 227
-- Name: empleados_id_empleados_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.empleados_id_empleados_seq', 4, true);


--
-- TOC entry 4891 (class 0 OID 0)
-- Dependencies: 231
-- Name: inventario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.inventario_id_seq', 1, true);


--
-- TOC entry 4892 (class 0 OID 0)
-- Dependencies: 219
-- Name: pedido_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pedido_id_seq', 1, true);


--
-- TOC entry 4893 (class 0 OID 0)
-- Dependencies: 229
-- Name: producto_id_producto_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.producto_id_producto_seq', 1, true);


--
-- TOC entry 4894 (class 0 OID 0)
-- Dependencies: 217
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 6, true);


--
-- TOC entry 4895 (class 0 OID 0)
-- Dependencies: 221
-- Name: venta_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.venta_id_seq', 3, true);


--
-- TOC entry 4699 (class 2606 OID 16826)
-- Name: clientes clientes_dni_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_dni_key UNIQUE (dni);


--
-- TOC entry 4701 (class 2606 OID 16824)
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id);


--
-- TOC entry 4697 (class 2606 OID 16779)
-- Name: detalle_venta detalle_venta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detalle_venta
    ADD CONSTRAINT detalle_venta_pkey PRIMARY KEY (id);


--
-- TOC entry 4703 (class 2606 OID 16852)
-- Name: empleados empleados_correo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empleados
    ADD CONSTRAINT empleados_correo_key UNIQUE (correo);


--
-- TOC entry 4705 (class 2606 OID 16850)
-- Name: empleados empleados_dni_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empleados
    ADD CONSTRAINT empleados_dni_key UNIQUE (dni);


--
-- TOC entry 4707 (class 2606 OID 16848)
-- Name: empleados empleados_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empleados
    ADD CONSTRAINT empleados_pkey PRIMARY KEY (id_empleados);


--
-- TOC entry 4711 (class 2606 OID 16879)
-- Name: inventario inventario_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventario
    ADD CONSTRAINT inventario_pkey PRIMARY KEY (id);


--
-- TOC entry 4693 (class 2606 OID 16692)
-- Name: pedido pedido_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pedido
    ADD CONSTRAINT pedido_pkey PRIMARY KEY (id);


--
-- TOC entry 4709 (class 2606 OID 16872)
-- Name: producto producto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.producto
    ADD CONSTRAINT producto_pkey PRIMARY KEY (id_producto);


--
-- TOC entry 4689 (class 2606 OID 16398)
-- Name: usuarios usuarios_nombre_usuario_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_nombre_usuario_key UNIQUE (nombre_usuario);


--
-- TOC entry 4691 (class 2606 OID 16396)
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- TOC entry 4695 (class 2606 OID 16762)
-- Name: venta venta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.venta
    ADD CONSTRAINT venta_pkey PRIMARY KEY (id);


--
-- TOC entry 4712 (class 2606 OID 16780)
-- Name: detalle_venta detalle_venta_id_venta_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.detalle_venta
    ADD CONSTRAINT detalle_venta_id_venta_fkey FOREIGN KEY (id_venta) REFERENCES public.venta(id);


--
-- TOC entry 4713 (class 2606 OID 16880)
-- Name: inventario inventario_id_producto_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventario
    ADD CONSTRAINT inventario_id_producto_fkey FOREIGN KEY (id_producto) REFERENCES public.producto(id_producto);


-- Completed on 2025-06-18 12:39:21

--
-- PostgreSQL database dump complete
--