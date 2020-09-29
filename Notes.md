# Implementation Notes

- En el archivo de muestra todos los tiempos coinciden en horas a en punto y a y media. Pero sólo se especifica que la ventana mínima es de media hora, no que tenga que ser algún múltiplo de 30 minutos. No se especifica en la documentación la precisión en las medidas de tiempo, así que se asume que es 1 minuto.
- Del dominio del probelma se entiende que el tiempo de comunicación y el de no comunicación son cíclicos, es decir, X minutos comunicando, seguidos de Y minutos sin comunicar, y otra vez inicio de X minutos comunicando: X, Y, X, Y, X, Y.......
- La suma de X + Y se entiende que es 24 horas = 1440 minutos. Esta suposición se basa en la información contenida en el fichero de ejemplo, ya que este contiene información temporal en formato hh:mm. Si el ciclo fuese de más de 24h se necesitaría especificar el paso de un día a otro.
- Si, en el fichero de entrada, el tiempo de inicio coincide con el tiempo de fin, se interpreta como que el satélite es geostacionario y que constantente se encuentra transmitiendo a la base. Siguiendo con el ejemplo anterior, sería el caso de (X = 1440, Y = 0).
- Si un satelite nunca transmitiese, es decir, el caso opuesto al aterior, (X = 0, Y = 1440), se entiende que simplemente dicho satélite no estaría en el fichero.
- Siguiendo el razonamiento anterior, el tiempo disponible se va a modelar como un vector de 1440 valores, en el que el índice 0 corresponde al punto temporal 00:00, y el índice 1439 corresponde a las 23:59.
- El concepto de total downlink se interpreta como los datos totales descargados durante una ventana de media hora. Equivaldría al cálculo de la integral de la curva de ratio, para un intervalo de media hora.
- No existe necesariamente unicidad en la ventana de media hora con máxima carga. Se pueden encontrar múltiples ventanas de 30 minutos con carga máxima. Por ejemplo, un único satétile geoestacionario produciría (1440 - 30) = 1410 ventanas de carga máxima. Por ello, el programa desarrollado devuelve un vector con los periodos de carga máxima encontrados.
- Este vector de periodos de carga máxima se escribe en un fichero de forma muy similar al tomado a la entrada. Es igual pero suprimiendo los campos de nombre y ratio de bajada, y añadiendo un último campo con la cantidad total de datos descargados durante el periodo: **hh:mm,hh:mm,total**

