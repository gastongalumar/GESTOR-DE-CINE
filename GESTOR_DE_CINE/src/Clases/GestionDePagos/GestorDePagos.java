
package Clases.GestionDePagos;

import Clases.GestionSelectorAsientos.AsientoButton;
import Clases.Funcion;
import Clases.GestionSelectorAsientos.SelectorAsientos;
import Clases.ListaGenerica;
import Enumeradores.EstadoAsiento;
import javafx.scene.control.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GestorDePagos {
    private ListaGenerica<Pago> listaPagos = new ListaGenerica<>();
    private ListaGenerica<MetodoDePago> metodoDePagos = new ListaGenerica<>();
    private SelectorAsientos selectorAsientos;

    // CONSTRUCTOR
    public GestorDePagos(SelectorAsientos selectorAsientos) {
        this.selectorAsientos = selectorAsientos;
    }

    // GETTER Y SETTER
    public ListaGenerica<Pago> getListaPagos() {
        return listaPagos;
    }

    public void setListaPagos(ListaGenerica<Pago> listaPagos) {
        this.listaPagos = listaPagos;
    }

    public ListaGenerica<MetodoDePago> getMetodoDePagos() {
        return metodoDePagos;
    }

    public void setMetodoDePagos(ListaGenerica<MetodoDePago> metodoDePagos) {
        this.metodoDePagos = metodoDePagos;
    }


    // MÉTODOS DE GESTIÓN DE PAGOS
    public void agregarMetodoDePago(MetodoDePago metodo) {
        metodoDePagos.agregar(metodo);
    }

    public void eliminarMetodoDePago(MetodoDePago metodo) {
        metodoDePagos.eliminar(metodo);
    }

    public void mostrarOpcionesDePago() {
        System.out.println("Opciones de pago disponibles:");
        for (MetodoDePago metodo : metodoDePagos.getElementos()) {
            System.out.println(metodo.getId() + ". " + metodo.getNombre());
        }
    }

    public static boolean procesarPago(MetodoDePago metodoPago, double totalAPagar, String descripcion) {
        System.out.println("Procesando pago de $" + totalAPagar + " usando " + metodoPago.getNombre() + " para: " + descripcion);
        return true;
    }

    // MÉTODOS DE ACTUALIZACIÓN DE UI
    public void actualizarContador() {
        if (selectorAsientos != null && selectorAsientos.getContadorLabel() != null) {
            int seleccionados = selectorAsientos.getSala().contarAsientosSeleccionados();
            selectorAsientos.getContadorLabel().setText(seleccionados + " asientos seleccionados");
        }
    }

    public void actualizarPrecioTotal() {
        if (selectorAsientos != null && selectorAsientos.getPrecioTotalLabel() != null) {
            double total = calcularPrecioTotal();
            selectorAsientos.getPrecioTotalLabel().setText(String.format("Total: $%,.2f", total));
        }
    }

    public double calcularPrecioTotal() {
        if (selectorAsientos == null || selectorAsientos.getSala() == null) return 0.0;

        int asientosSeleccionados = selectorAsientos.getSala().contarAsientosSeleccionados();
        double precioUnitario = selectorAsientos.getFuncion() != null ? selectorAsientos.getFuncion().getPrecio() : 5000.0;
        return asientosSeleccionados * precioUnitario;
    }

    // METODO PRINCIPAL DE PROCESAMIENTO DE PAGO
    public void procesarPago() {
        double totalAPagar = calcularPrecioTotal();
        List<String> asientosSeleccionadosParaTicket = obtenerAsientosSeleccionados();
        int cantidadAsientos = asientosSeleccionadosParaTicket.size();

        if (cantidadAsientos == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Selecciones");
            alert.setHeaderText("No hay asientos seleccionados para pagar.");
            alert.setContentText("Selecciona algunos asientos primero.");
            alert.showAndWait();
            return;
        }

        // Mostrar resumen de compra
        String resumen = crearResumenCompra(cantidadAsientos, totalAPagar);

        MetodoDePago TarjetaCredito = new MetodoDePago(generarIdMetodoPago(), "Tarjeta de Crédito");
        MetodoDePago TarjetaDebito = new MetodoDePago(generarIdMetodoPago(), "Tarjeta de Débito");
        MetodoDePago CuponEfectivo = new MetodoDePago(generarIdMetodoPago(), "Generar Cupón Pago en Efectivo");
        MetodoDePago TransferenciaBancaria = new MetodoDePago(generarIdMetodoPago(), "Transferencia Bancaria");

        agregarMetodoDePago(TarjetaCredito);
        agregarMetodoDePago(TarjetaDebito);
        agregarMetodoDePago(CuponEfectivo);
        agregarMetodoDePago(TransferenciaBancaria);

        ChoiceDialog<MetodoDePago> dialog = new ChoiceDialog<>(TarjetaCredito,TarjetaCredito, TarjetaDebito, CuponEfectivo, TransferenciaBancaria);


        dialog.setTitle("Procesar Pago");
        dialog.setHeaderText("💳 PROCESAR PAGO");
        dialog.setContentText(resumen);

        Optional<MetodoDePago> resultado = dialog.showAndWait();

        if (resultado.isPresent()) {
            MetodoDePago metodoSeleccionado = resultado.get();
            procesarPagoConMetodo(String.valueOf(metodoSeleccionado), totalAPagar, cantidadAsientos, asientosSeleccionadosParaTicket);
        }
    }

    private String crearResumenCompra(int cantidadAsientos, double totalAPagar) {
        Funcion funcion = selectorAsientos.getFuncion();
        return String.format(
                "📋 RESUMEN DE COMPRA\n\n" +
                        "🎬 Película: %s\n" +
                        "⏰ Función: %s\n" +
                        "🎫 Asientos seleccionados: %d\n" +
                        "💰 Precio por asiento: $%,.2f\n" +
                        "💵 TOTAL A PAGAR: $%,.2f\n\n" +
                        "Seleccione el método de pago:",
                funcion != null && funcion.getPelicula() != null ? funcion.getPelicula().getNombrePelicula() : "No especificada",
                funcion != null && funcion.getHorarioFuncion() != null ?
                        funcion.getHorarioFuncion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "No especificada",
                cantidadAsientos,
                funcion != null ? funcion.getPrecio() : 5000.0,
                totalAPagar
        );
    }

    private void procesarPagoConMetodo(String metodoSeleccionado, double totalAPagar, int cantidadAsientos, List<String> asientosSeleccionados) {
        MetodoDePago metodoPago = new MetodoDePago(generarIdMetodoPago(), metodoSeleccionado);
        String descripcion = String.format("Compra de %d asientos para %s",
                cantidadAsientos,
                selectorAsientos.getFuncion() != null && selectorAsientos.getFuncion().getPelicula() != null ?
                        selectorAsientos.getFuncion().getPelicula().getNombrePelicula() : "película"
        );

        boolean pagoExitoso = procesarPago(metodoPago, totalAPagar, descripcion);

        if (pagoExitoso) {
            int asientosConfirmados = confirmarSelecciones();
            mostrarConfirmacionPago(metodoSeleccionado, totalAPagar, cantidadAsientos, asientosSeleccionados);
        }
    }

    private void mostrarConfirmacionPago(String metodoSeleccionado, double totalAPagar, int cantidadAsientos, List<String> asientosSeleccionados) {
        Alert exito = new Alert(Alert.AlertType.INFORMATION);
        exito.setTitle("Pago Exitoso");
        exito.setHeaderText("✅ PAGO PROCESADO EXITOSAMENTE");
        exito.setContentText(String.format(
                "Método: %s\n" +
                        "Monto: $%,.2f\n" +
                        "Asientos: %d\n" +
                        "¡Disfrute de la función!",
                metodoSeleccionado, totalAPagar, cantidadAsientos
        ));

        ButtonType btnImprimirTicket = new ButtonType("🎫 Imprimir Ticket");
        ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        exito.getButtonTypes().setAll(btnImprimirTicket, btnCerrar);

        Optional<ButtonType> resultado2 = exito.showAndWait();

        if (resultado2.isPresent() && resultado2.get() == btnImprimirTicket) {
            imprimirTicketCompra(metodoSeleccionado, totalAPagar, asientosSeleccionados);
        }

    }

    private int confirmarSelecciones() {
        if (selectorAsientos == null || selectorAsientos.getGestorJson() == null) return 0;

        int seleccionados = selectorAsientos.getSala().contarAsientosSeleccionados();

        if (seleccionados == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Selecciones");
            alert.setHeaderText("No hay asientos seleccionados para confirmar.");
            alert.setContentText("Selecciona algunos asientos primero.");
            alert.showAndWait();
            return seleccionados;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Selección");
        alert.setHeaderText("¿Confirmar " + seleccionados + " asiento(s) seleccionado(s)?");
        alert.setContentText("✅ Los asientos seleccionados (azules) pasarán a OCUPADOS (rojos)\n🔒 No podrán ser modificados después");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            int confirmados = selectorAsientos.getGestorJson().confirmarSelecciones();
            selectorAsientos.actualizarVisualizacionAsientos();

            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Confirmación Exitosa");
            exito.setHeaderText("✅ " + confirmados + " asiento(s) confirmado(s) exitosamente!");
            exito.setContentText("🔴 Ahora aparecen en ROJO (ocupados)\n💾 Guardados correctamente!!!\n🔒 Ya no se pueden modificar");
            exito.showAndWait();
            return seleccionados;
        }
        return seleccionados;
    }

    // MÉTODOS PARA TICKET
    private void imprimirTicketCompra(String metodoPago, double monto, List<String> asientosSeleccionados) {
        try {
            Funcion funcion = selectorAsientos.getFuncion();
            String nombrePelicula = funcion.getPelicula().getNombrePelicula();
            String horario = funcion.getHorarioFuncion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String salaNombre = funcion.getSala() != null ? funcion.getSala().getNombreSala() : "Sala Principal";
            String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            String asientosStr = asientosSeleccionados.isEmpty() ? "Por asignar" : String.join(", ", asientosSeleccionados);

            System.out.println("🔍 Asientos seleccionados para ticket: " + asientosSeleccionados);
            System.out.println("🔍 Total de asientos: " + asientosSeleccionados.size());

            String numeroTicket = generarNumeroTicket();
            String codigoOR = "OR-CMX-" + numeroTicket.replace("TK", "");

            String fileName = "ticket_cine_" + System.currentTimeMillis() + ".html";
            java.io.FileWriter writer = new java.io.FileWriter(fileName);

            String qrImagePath = obtenerRutaAbsolutaImagen("qr.jpg");
            String barcodeImagePath = obtenerRutaAbsolutaImagen("bar.jpg");

            // Escribir HTML completo del ticket
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html>\n");
            writer.write("<head>\n");
            writer.write("    <title>Ticket de Cine - CINEMAX</title>\n");
            writer.write("    <meta charset=\"UTF-8\">\n");
            writer.write("    <style>\n");
            writer.write("        body { \n");
            writer.write("            font-family: Arial, sans-serif; \n");
            writer.write("            margin: 0; \n");
            writer.write("            padding: 20px;\n");
            writer.write("            background: #f0f0f0;\n");
            writer.write("        }\n");
            writer.write("        .ticket-container {\n");
            writer.write("            max-width: 480px;\n");
            writer.write("            margin: 0 auto;\n");
            writer.write("            background: white;\n");
            writer.write("            border: 2px solid #333;\n");
            writer.write("            border-radius: 8px;\n");
            writer.write("            box-shadow: 0 4px 12px rgba(0,0,0,0.3);\n");
            writer.write("        }\n");
            writer.write("        .header {\n");
            writer.write("            text-align: center;\n");
            writer.write("            background: linear-gradient(135deg, #1a237e, #283593);\n");
            writer.write("            color: white;\n");
            writer.write("            padding: 12px;\n");
            writer.write("            border-radius: 6px 6px 0 0;\n");
            writer.write("        }\n");
            writer.write("        .header h1 {\n");
            writer.write("            margin: 0;\n");
            writer.write("            font-size: 22px;\n");
            writer.write("            font-weight: bold;\n");
            writer.write("        }\n");
            writer.write("        .ticket-info {\n");
            writer.write("            padding: 15px;\n");
            writer.write("            background: white;\n");
            writer.write("        }\n");
            writer.write("        .info-section {\n");
            writer.write("            margin-bottom: 12px;\n");
            writer.write("            padding: 10px;\n");
            writer.write("            background: #f8f9fa;\n");
            writer.write("            border-radius: 5px;\n");
            writer.write("            border-left: 4px solid #1a237e;\n");
            writer.write("        }\n");
            writer.write("        .info-row {\n");
            writer.write("            display: flex;\n");
            writer.write("            justify-content: space-between;\n");
            writer.write("            margin-bottom: 5px;\n");
            writer.write("            padding-bottom: 3px;\n");
            writer.write("            border-bottom: 1px dashed #ddd;\n");
            writer.write("        }\n");
            writer.write("        .info-label {\n");
            writer.write("            font-weight: bold;\n");
            writer.write("            color: #333;\n");
            writer.write("            min-width: 120px;\n");
            writer.write("        }\n");
            writer.write("        .info-value {\n");
            writer.write("            color: #555;\n");
            writer.write("            text-align: right;\n");
            writer.write("            flex: 1;\n");
            writer.write("        }\n");
            writer.write("        .total-section {\n");
            writer.write("            background: #e8f5e8;\n");
            writer.write("            padding: 12px;\n");
            writer.write("            margin: 12px 0;\n");
            writer.write("            border: 2px solid #4caf50;\n");
            writer.write("            border-radius: 5px;\n");
            writer.write("            text-align: center;\n");
            writer.write("        }\n");
            writer.write("        .total-amount {\n");
            writer.write("            font-size: 20px;\n");
            writer.write("            font-weight: bold;\n");
            writer.write("            color: #2e7d32;\n");
            writer.write("        }\n");
            writer.write("        .codes-section {\n");
            writer.write("            display: flex;\n");
            writer.write("            justify-content: space-between;\n");
            writer.write("            background: #f5f5f5;\n");
            writer.write("            padding: 12px;\n");
            writer.write("            margin: 12px 0;\n");
            writer.write("            border-radius: 5px;\n");
            writer.write("            gap: 10px;\n");
            writer.write("        }\n");
            writer.write("        .code-block {\n");
            writer.write("            flex: 1;\n");
            writer.write("            text-align: center;\n");
            writer.write("            padding: 8px;\n");
            writer.write("        }\n");
            writer.write("        .barcode-simple {\n");
            writer.write("            font-family: 'Courier New', monospace;\n");
            writer.write("            font-size: 12px;\n");
            writer.write("            letter-spacing: 3px;\n");
            writer.write("            font-weight: bold;\n");
            writer.write("            margin: 5px 0;\n");
            writer.write("            padding: 6px;\n");
            writer.write("            background: white;\n");
            writer.write("            border: 1px solid #ccc;\n");
            writer.write("            border-radius: 3px;\n");
            writer.write("            color: #333;\n");
            writer.write("        }\n");
            writer.write("        .numeric-code {\n");
            writer.write("            font-family: 'Courier New', monospace;\n");
            writer.write("            font-size: 11px;\n");
            writer.write("            font-weight: bold;\n");
            writer.write("            margin: 4px 0;\n");
            writer.write("            padding: 5px;\n");
            writer.write("            background: white;\n");
            writer.write("            border: 1px solid #ccc;\n");
            writer.write("            border-radius: 3px;\n");
            writer.write("            color: #333;\n");
            writer.write("        }\n");
            writer.write("        .footer {\n");
            writer.write("            text-align: center;\n");
            writer.write("            padding: 10px;\n");
            writer.write("            background: #333;\n");
            writer.write("            color: white;\n");
            writer.write("            border-radius: 0 0 6px 6px;\n");
            writer.write("            font-size: 10px;\n");
            writer.write("        }\n");
            writer.write("        .important-info {\n");
            writer.write("            background: #fff3cd;\n");
            writer.write("            padding: 8px;\n");
            writer.write("            margin: 8px 0;\n");
            writer.write("            border: 1px solid #ffeaa7;\n");
            writer.write("            border-radius: 4px;\n");
            writer.write("            font-size: 10px;\n");
            writer.write("        }\n");
            writer.write("        .promo-section {\n");
            writer.write("            background: #e3f2fd;\n");
            writer.write("            padding: 6px;\n");
            writer.write("            margin: 8px 0;\n");
            writer.write("            border: 1px solid #90caf9;\n");
            writer.write("            border-radius: 4px;\n");
            writer.write("            text-align: center;\n");
            writer.write("            font-size: 10px;\n");
            writer.write("        }\n");
            writer.write("        .img-container {\n");
            writer.write("            width: 80px;\n");
            writer.write("            height: 80px;\n");
            writer.write("            margin: 5px auto;\n");
            writer.write("            border: 2px solid #333;\n");
            writer.write("            border-radius: 4px;\n");
            writer.write("            overflow: hidden;\n");
            writer.write("            display: flex;\n");
            writer.write("            align-items: center;\n");
            writer.write("            justify-content: center;\n");
            writer.write("            background: white;\n");
            writer.write("        }\n");
            writer.write("        .img-container img {\n");
            writer.write("            max-width: 100%;\n");
            writer.write("            max-height: 100%;\n");
            writer.write("            object-fit: contain;\n");
            writer.write("        }\n");
            writer.write("    </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("    <div class=\"ticket-container\">\n");
            writer.write("        <div class=\"header\">\n");
            writer.write("            <h1>🎬 CINEMAX THEATRES </h1>\n");
            writer.write("            <div style=\"font-size: 11px; margin-top: 3px;\">¡Gracias por su compra!</div>\n");
            writer.write("        </div>\n");
            writer.write("        \n");
            writer.write("        <div class=\"ticket-info\">\n");
            writer.write("            <div class=\"info-section\">\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Ticket #:</span>\n");
            writer.write("                    <span class=\"info-value\">" + numeroTicket + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Emisión:</span>\n");
            writer.write("                    <span class=\"info-value\">" + timestamp + "</span>\n");
            writer.write("                </div>\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"info-section\">\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Película:</span>\n");
            writer.write("                    <span class=\"info-value\">" + nombrePelicula + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Función:</span>\n");
            writer.write("                    <span class=\"info-value\">" + horario + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Sala:</span>\n");
            writer.write("                    <span class=\"info-value\">" + salaNombre + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Asientos:</span>\n");
            writer.write("                    <span class=\"info-value\">" + asientosStr + "</span>\n");
            writer.write("                </div>\n");
            writer.write("                <div class=\"info-row\">\n");
            writer.write("                    <span class=\"info-label\">Método Pago:</span>\n");
            writer.write("                    <span class=\"info-value\">" + metodoPago + "</span>\n");
            writer.write("                </div>\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"total-section\">\n");
            writer.write("                <div class=\"total-amount\">TOTAL: $" + String.format("%,.2f", monto) + "</div>\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"codes-section\">\n");
            writer.write("                <div class=\"code-block\">\n");
            writer.write("                    <strong style=\"font-size: 11px;\">📋 CÓDIGO OR</strong>\n");
            writer.write("                    <div class=\"numeric-code\" style=\"font-size: 10px; padding: 4px;\">" + codigoOR + "</div>\n");
            writer.write("                    <small style=\"font-size: 8px; color: #666;\">Orden de compra</small>\n");
            writer.write("                </div>\n");
            writer.write("                \n");
            writer.write("                <div class=\"code-block\">\n");
            writer.write("                    <strong style=\"font-size: 11px;\">📊 CÓDIGO BARRAS</strong>\n");
            writer.write("                    <div class=\"img-container\" style=\"width: 150px; height: 100px;\">\n");
            writer.write("                        <img src=\"" + barcodeImagePath + "\" alt=\"Código de Barras\" onerror=\"this.style.display='none'; this.parentNode.innerHTML='<div style=&quot;padding:10px;text-align:center;color:#660;&quot;>Imagen no disponible</div>';\">\n");
            writer.write("                    </div>\n");
            writer.write("                    <div class=\"numeric-code\" style=\"font-size: 9px; margin-top: 3px;\">" + numeroTicket.replace("TK", "") + "</div>\n");
            writer.write("                    <small style=\"font-size: 8px; color: #666;\">Escaneo rápido</small>\n");
            writer.write("                </div>\n");
            writer.write("                \n");
            writer.write("                <div class=\"code-block\">\n");
            writer.write("                    <strong style=\"font-size: 11px;\">🔳 CÓDIGO QR</strong>\n");
            writer.write("                    <div class=\"img-container\" style=\"width: 100px; height: 100px;\">\n");
            writer.write("                        <img src=\"" + qrImagePath + "\" alt=\"Código QR\" onerror=\"this.style.display='none'; this.parentNode.innerHTML='<div style=&quot;padding:10px;text-align:center;color:#666;&quot;>Imagen no disponible</div>';\">\n");
            writer.write("                    </div>\n");
            writer.write("                    <div class=\"numeric-code\" style=\"font-size: 9px; margin-top: 3px;\">" + numeroTicket.replace("TK", "").substring(0, 6) + "</div>\n");
            writer.write("                    <small style=\"font-size: 8px; color: #666;\">Escaneo móvil</small>\n");
            writer.write("                </div>\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"important-info\">\n");
            writer.write("                <strong>📋 INFORMACIÓN IMPORTANTE</strong><br>\n");
            writer.write("                • Presente este ticket en la entrada<br>\n");
            writer.write("                • Llegue con 20 min de anticipación<br>\n");
            writer.write("                • No se permiten reembolsos\n");
            writer.write("            </div>\n");
            writer.write("            \n");
            writer.write("            <div class=\"promo-section\">\n");
            writer.write("                <strong>📱 ¡Descargue nuestra App!</strong><br>\n");
            writer.write("                Obtenga un <strong>combo GRATIS</strong>\n");
            writer.write("            </div>\n");
            writer.write("        </div>\n");
            writer.write("        \n");
            writer.write("        <div class=\"footer\">\n");
            writer.write("            ¡Disfrute de la función!<br>\n");
            writer.write("            @CinemaxTheaters #CinemaxExperience\n");
            writer.write("        </div>\n");
            writer.write("    </div>\n");
            writer.write("</body>\n");
            writer.write("</html>\n");

            writer.close();
            abrirEnNavegador(fileName);

        } catch (Exception e) {
            mostrarAlertaError(e);
        }
    }

    private List<String> obtenerAsientosSeleccionados() {
        List<String> asientos = new ArrayList<>();
        System.out.println("🔍 BUSCANDO ASIENTOS SELECCIONADOS...");

        for (int filaIndex = 0; filaIndex < selectorAsientos.getFilasAsientos(); filaIndex++) {
            for (int colIndex = 0; colIndex < selectorAsientos.getColumnas(); colIndex++) {
                if (selectorAsientos.getBotonesAsientos()[filaIndex][colIndex] != null) {
                    AsientoButton boton = selectorAsientos.getBotonesAsientos()[filaIndex][colIndex];

                    if (boton.getEstado() == EstadoAsiento.SELECCIONADO) {
                        String letraFila = String.valueOf((char) ('A' + filaIndex));
                        int numeroColumna = -1;

                        for (int i = 0; i < selectorAsientos.getColumnasValidas().size(); i++) {
                            if (selectorAsientos.getColumnasValidas().get(i) == colIndex) {
                                numeroColumna = i + 1;
                                break;
                            }
                        }

                        if (numeroColumna != -1) {
                            String asiento = letraFila + numeroColumna;
                            asientos.add(asiento);
                            System.out.println("✅ Asiento seleccionado: " + asiento + " (fila=" + filaIndex + ", col=" + colIndex + ")");
                        }
                    }
                }
            }
        }

        System.out.println("🔍 Total de asientos encontrados: " + asientos.size());
        System.out.println("🔍 Asientos: " + asientos);
        return asientos;
    }

    // MÉTODOS AUXILIARES
    private String generarNumeroTicket() {
        Random random = new Random();
        int numero = random.nextInt(900000) + 100000;
        return "TK" + numero;
    }

    private int generarIdMetodoPago() {
        return (int) (System.currentTimeMillis() % 1000000);
    }

    private String obtenerRutaAbsolutaImagen(String rutaRelativa) {
        try {
            java.io.File archivoImagen = new java.io.File(rutaRelativa);
            if (archivoImagen.exists()) {
                return archivoImagen.toURI().toString();
            } else {
                System.err.println("⚠️ No se encontró la imagen: " + rutaRelativa);
                String[] rutasAlternativas = {
                        "src/" + rutaRelativa,
                        "resources/" + rutaRelativa,
                        "./" + rutaRelativa,
                        "../" + rutaRelativa
                };

                for (String rutaAlt : rutasAlternativas) {
                    archivoImagen = new java.io.File(rutaAlt);
                    if (archivoImagen.exists()) {
                        System.out.println("✅ Imagen encontrada en: " + rutaAlt);
                        return archivoImagen.toURI().toString();
                    }
                }

                System.err.println("❌ No se pudo encontrar la imagen en ninguna ubicación alternativa");
                return "";
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener ruta de imagen: " + e.getMessage());
            return "";
        }
    }

    private void abrirEnNavegador(String fileName) {
        try {
            java.io.File file = new java.io.File(fileName);
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
                System.out.println("🌐 Navegador abierto con el ticket: " + fileName);
            }
        } catch (Exception e) {
            System.err.println("❌ No se pudo abrir automáticamente: " + e.getMessage());

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Ticket Listo");
            info.setHeaderText("📄 TICKET GENERADO");
            info.setContentText("El ticket se guardó como: " + fileName +
                    "\n\nPuedes abrirlo manualmente desde:\n" +
                    new java.io.File(".").getAbsolutePath() +
                    "\n\nBusca el archivo y ábrelo con tu navegador.");
            info.showAndWait();
        }
    }

    private void mostrarAlertaError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al Generar Ticket");
        alert.setHeaderText("❌ ERROR AL GENERAR EL TICKET");
        alert.setContentText("Ocurrió un error al generar el ticket:\n" + e.getMessage());
        alert.showAndWait();
    }
}