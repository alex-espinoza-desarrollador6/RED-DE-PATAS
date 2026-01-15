/* ========================================================== */
/* ESPECIFICACIÓN DEL PAQUETE                                 */
/* ========================================================== */
CREATE OR REPLACE PACKAGE MSIRDP.PKG_MSIRDP_AUTENTICACION IS

   TYPE t_cursor IS REF CURSOR;

   /*
      AUTOR: Clinton Espinoza
      FECHA DE CREACION: 14/01/2026
      DESCRIPCION: Procedimiento para validar credenciales de usuario
      ARGUMENTOS:
         PvcCODDNI: DNI del usuario
         PvcTXTCLAVE: Contraseña
         PcrDATOS: Cursor de salida con datos del usuario
   */
   PROCEDURE PRC_VALIDARUSUARIO(
      PvcCODDNI   IN VARCHAR2,
      PvcTXTCLAVE IN VARCHAR2,
      PcrDATOS    OUT t_cursor
   );

END PKG_MSIRDP_AUTENTICACION;
/

/* ========================================================== */
/* CUERPO DEL PAQUETE                                         */
/* ========================================================== */
CREATE OR REPLACE PACKAGE BODY MSIRDP.PKG_MSIRDP_AUTENTICACION IS

   PROCEDURE PRC_VALIDARUSUARIO(
      PvcCODDNI   IN VARCHAR2,
      PvcTXTCLAVE IN VARCHAR2,
      PcrDATOS    OUT t_cursor
   ) IS
   /*
      AUTOR: Clinton Espinoza
      FECHA DE CREACION: 14/01/2026
      FECHA DE MODIFICACION:
      DESCRIPCION: Valida usuario por DNI y Clave
   */
BEGIN
      /* Se valida credenciales cruzando Persona y Usuario */
OPEN PcrDATOS FOR
SELECT
    U.NUMUSUARIO,
    U.TXTCORREO,
    P.NUMPERSONA,
    P.CODDNI,
    P.TXTNOMBRES,
    P.TXTAPELLIDOS,
    R.NUMROL,
    R.TXTNOMBRE AS ROL
FROM MSIRDP.RDP_USUARIO U
         INNER JOIN MSIRDP.RDP_PERSONA P ON U.NUMPERSONA = P.NUMPERSONA
         INNER JOIN MSIRDP.RDP_ROL R ON U.NUMROL = R.NUMROL
WHERE P.CODDNI = PvcCODDNI
  AND U.TXTCLAVE = PvcTXTCLAVE
  AND U.ESTUSUARIO = 'ACT'
  AND P.FLGACTIVO = '1';

EXCEPTION
      WHEN OTHERS THEN
         RAISE_APPLICATION_ERROR(-20001, 'Error en autenticación: ' || SQLERRM);
END PRC_VALIDARUSUARIO;

END PKG_MSIRDP_AUTENTICACION;
/



/* ========================================================== */
/* ESPECIFICACIÓN DEL PAQUETE                                 */
/* ========================================================== */
CREATE OR REPLACE PACKAGE MSIRDP.PKG_MSIRDP_MANTUSUARIOS IS

   TYPE t_cursor IS REF CURSOR;

   /* Obtener detalle de usuario por DNI */
   PROCEDURE PRC_OBTENERUSUARIO(
      PvcCODDNI IN VARCHAR2,
      PcrDATOS  OUT t_cursor
   );

   /* Listar usuarios (filtro opcional por Rol) */
   PROCEDURE PRC_LISTARUSUARIOS(
      PvcTXTNOMBREROL IN VARCHAR2 DEFAULT NULL, -- Opcional
      PcrLISTA        OUT t_cursor
   );

   /* Actualizar datos del usuario mediante DNI */
   PROCEDURE PRC_ACTUALIZARUSUARIO(
      PvcCODDNI       IN VARCHAR2,
      PvcTXTNOMBRES   IN VARCHAR2,
      PvcTXTAPELLIDOS IN VARCHAR2,
      PvcTXTTELEFONO  IN VARCHAR2,
      PvcTXTDIRECCION IN VARCHAR2,
      PvcTXTCORREO    IN VARCHAR2,
      PvcTXTCLAVE     IN VARCHAR2 -- Se asume cambio de clave opcional en logica
   );

   /* Dar de baja (Logica: ESTUSUARIO = INA) */
   PROCEDURE PRC_DARBAJAUSUARIO(
      PvcCODDNI IN VARCHAR2
   );

END PKG_MSIRDP_MANTUSUARIOS;
/

/* ========================================================== */
/* CUERPO DEL PAQUETE                                         */
/* ========================================================== */
CREATE OR REPLACE PACKAGE BODY MSIRDP.PKG_MSIRDP_MANTUSUARIOS IS

   /* ------------------------------------------------------- */
   /* PRC_OBTENERUSUARIO                                      */
   /* ------------------------------------------------------- */
   PROCEDURE PRC_OBTENERUSUARIO(
      PvcCODDNI IN VARCHAR2,
      PcrDATOS  OUT t_cursor
   ) IS
   /*
      AUTOR: Clinton Espinoza
      FECHA DE CREACION: 14/01/2026
      DESCRIPCION: Obtiene el detalle completo de un usuario por su DNI
   */
BEGIN
OPEN PcrDATOS FOR
SELECT
    P.CODDNI,
    P.TXTNOMBRES,
    P.TXTAPELLIDOS,
    P.TXTTELEFONO,
    P.TXTDIRECCION,
    U.TXTCORREO,
    R.TXTNOMBRE AS ROL,
    U.ESTUSUARIO
FROM MSIRDP.RDP_PERSONA P
         INNER JOIN MSIRDP.RDP_USUARIO U ON P.NUMPERSONA = U.NUMPERSONA
         INNER JOIN MSIRDP.RDP_ROL R ON U.NUMROL = R.NUMROL
WHERE P.CODDNI = PvcCODDNI;

EXCEPTION
      WHEN OTHERS THEN
         RAISE_APPLICATION_ERROR(-20001, 'Error al obtener usuario: ' || SQLERRM);
END PRC_OBTENERUSUARIO;

   /* ------------------------------------------------------- */
   /* PRC_LISTARUSUARIOS                                      */
   /* ------------------------------------------------------- */
   PROCEDURE PRC_LISTARUSUARIOS(
      PvcTXTNOMBREROL IN VARCHAR2 DEFAULT NULL,
      PcrLISTA        OUT t_cursor
   ) IS
   /*
      AUTOR: Clinton Espinoza
      FECHA DE CREACION: 14/01/2026
      DESCRIPCION: Lista usuarios. Si PvcTXTNOMBREROL es NULL, lista todos.
   */
BEGIN
      IF PvcTXTNOMBREROL IS NULL THEN
         OPEN PcrLISTA FOR
SELECT P.CODDNI, P.TXTNOMBRES, P.TXTAPELLIDOS, U.TXTCORREO, R.TXTNOMBRE
FROM MSIRDP.RDP_USUARIO U
         JOIN MSIRDP.RDP_PERSONA P ON U.NUMPERSONA = P.NUMPERSONA
         JOIN MSIRDP.RDP_ROL R ON U.NUMROL = R.NUMROL
ORDER BY P.TXTAPELLIDOS;
ELSE
         OPEN PcrLISTA FOR
SELECT P.CODDNI, P.TXTNOMBRES, P.TXTAPELLIDOS, U.TXTCORREO, R.TXTNOMBRE
FROM MSIRDP.RDP_USUARIO U
         JOIN MSIRDP.RDP_PERSONA P ON U.NUMPERSONA = P.NUMPERSONA
         JOIN MSIRDP.RDP_ROL R ON U.NUMROL = R.NUMROL
WHERE R.TXTNOMBRE = PvcTXTNOMBREROL
ORDER BY P.TXTAPELLIDOS;
END IF;

EXCEPTION
      WHEN OTHERS THEN
         RAISE_APPLICATION_ERROR(-20001, 'Error al listar usuarios: ' || SQLERRM);
END PRC_LISTARUSUARIOS;

   /* ------------------------------------------------------- */
   /* PRC_ACTUALIZARUSUARIO                                   */
   /* ------------------------------------------------------- */
   PROCEDURE PRC_ACTUALIZARUSUARIO(
      PvcCODDNI       IN VARCHAR2,
      PvcTXTNOMBRES   IN VARCHAR2,
      PvcTXTAPELLIDOS IN VARCHAR2,
      PvcTXTTELEFONO  IN VARCHAR2,
      PvcTXTDIRECCION IN VARCHAR2,
      PvcTXTCORREO    IN VARCHAR2,
      PvcTXTCLAVE     IN VARCHAR2
   ) IS
      LnmNUMPERSONA NUMBER(12);
   /*
      AUTOR: Clinton Espinoza
      FECHA DE CREACION: 14/01/2026
      DESCRIPCION: Actualiza datos personales y de cuenta por DNI
   */
BEGIN
      /* Obtenemos el ID interno */
SELECT NUMPERSONA INTO LnmNUMPERSONA
FROM MSIRDP.RDP_PERSONA
WHERE CODDNI = PvcCODDNI;

/* Actualizacion Tabla Padre (PERSONA) */
UPDATE MSIRDP.RDP_PERSONA
SET TXTNOMBRES   = PvcTXTNOMBRES,
    TXTAPELLIDOS = PvcTXTAPELLIDOS,
    TXTTELEFONO  = PvcTXTTELEFONO,
    TXTDIRECCION = PvcTXTDIRECCION,
    FECMODIFICA  = SYSDATE,
    TXTUSUMOD    = 'PKG_MANT'
WHERE NUMPERSONA = LnmNUMPERSONA;

/* Actualizacion Tabla Hija (USUARIO) */
UPDATE MSIRDP.RDP_USUARIO
SET TXTCORREO   = PvcTXTCORREO,
    /* Si envian clave nueva se actualiza, sino se mantiene (logica simple) */
    TXTCLAVE    = NVL(PvcTXTCLAVE, TXTCLAVE),
    FECMODIFICA = SYSDATE,
    TXTUSUMOD   = 'PKG_MANT'
WHERE NUMPERSONA = LnmNUMPERSONA;

COMMIT;

EXCEPTION
      WHEN NO_DATA_FOUND THEN
         RAISE_APPLICATION_ERROR(-20002, 'El usuario con DNI ' || PvcCODDNI || ' no existe.');
WHEN OTHERS THEN
         ROLLBACK;
         RAISE_APPLICATION_ERROR(-20002, 'Error al actualizar: ' || SQLERRM);
END PRC_ACTUALIZARUSUARIO;

   /* ------------------------------------------------------- */
   /* PRC_DARBAJAUSUARIO                                      */
   /* ------------------------------------------------------- */
   PROCEDURE PRC_DARBAJAUSUARIO(
      PvcCODDNI IN VARCHAR2
   ) IS
      LnmNUMPERSONA NUMBER(12);
   /*
      AUTOR: Clinton Espinoza
      FECHA DE CREACION: 14/01/2026
      DESCRIPCION: Baja logica (Inactiva usuario y persona)
   */
BEGIN
SELECT NUMPERSONA INTO LnmNUMPERSONA
FROM MSIRDP.RDP_PERSONA
WHERE CODDNI = PvcCODDNI;

/* Baja logica en Persona */
UPDATE MSIRDP.RDP_PERSONA
SET FLGACTIVO   = '0',
    FECMODIFICA = SYSDATE
WHERE NUMPERSONA = LnmNUMPERSONA;

/* Baja logica en Usuario (Estado = INA) */
UPDATE MSIRDP.RDP_USUARIO
SET ESTUSUARIO  = 'INA',
    FECMODIFICA = SYSDATE
WHERE NUMPERSONA = LnmNUMPERSONA;

COMMIT;

EXCEPTION
      WHEN NO_DATA_FOUND THEN
         RAISE_APPLICATION_ERROR(-20002, 'El usuario con DNI ' || PvcCODDNI || ' no existe.');
WHEN OTHERS THEN
         ROLLBACK;
         RAISE_APPLICATION_ERROR(-20002, 'Error al dar de baja: ' || SQLERRM);
END PRC_DARBAJAUSUARIO;

END PKG_MSIRDP_MANTUSUARIOS;
/

