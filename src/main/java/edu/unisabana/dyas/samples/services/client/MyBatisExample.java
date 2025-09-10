/*
 * Copyright (C) 2015 cesarvefe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

 package edu.unisabana.dyas.samples.services.client;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import edu.unisabana.dyas.sampleprj.dao.mybatis.mappers.ClienteMapper;
import edu.unisabana.dyas.sampleprj.dao.mybatis.mappers.ItemMapper;
import edu.unisabana.dyas.sampleprj.dao.mybatis.mappers.TipoItemMapper;
import edu.unisabana.dyas.samples.entities.Cliente;
import edu.unisabana.dyas.samples.entities.Item;
import edu.unisabana.dyas.samples.entities.TipoItem;

/**
 *
 * @author cesarvefe
 */
public class MyBatisExample {

    /**
     * Método que construye una fábrica de sesiones de MyBatis a partir del
     * archivo de configuración ubicado en src/main/resources
     *
     * @return instancia de SQLSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        SqlSessionFactory sqlSessionFactory = null;
        if (sqlSessionFactory == null) {
            InputStream inputStream;
            try {
                inputStream = Resources.getResourceAsStream("mybatis-config.xml");
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e.getCause());
            }
        }
        return sqlSessionFactory;
    }

    /**
     * Programa principal de ejempo de uso de MyBATIS
     * @param args
     * @throws SQLException 
     */
    public static void main(String args[]) throws SQLException {

        SqlSessionFactory sessionfact = getSqlSessionFactory();
        SqlSession sqlss = sessionfact.openSession();

        ClienteMapper cm=sqlss.getMapper(ClienteMapper.class);
        ItemMapper im = sqlss.getMapper(ItemMapper.class);
        TipoItemMapper tim = sqlss.getMapper(TipoItemMapper.class);

        // Consultar todos los clientes de la base de datos
        List<Cliente> clientes = cm.consultarClientes();
        System.out.println(clientes);

        // Consultar un cliente especifico
        Cliente cliente = cm.consultarCliente(123456789); // documento del cliente
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Items rentados:");
        cliente.getRentados().forEach(ir -> {
            System.out.println("  Item: " + ir.getItem().getNombre() +
                                " desde " + ir.getFechainiciorenta() +
                                " hasta " + ir.getFechafinrenta());
        });

        // Agregar un item a un usuario en especifico
        cm.agregarItemRentadoACliente(123456789, 10, Date.valueOf("2025-09-09"), Date.valueOf("2025-09-15"));
        System.out.println("Item rentado agregado correctamente al cliente 123.");

        // Insertar Item
        TipoItem tipo = new TipoItem(1, "Pelicula"); // debe existir en DB
        Item nuevo = new Item(
                                tipo, // tipo
                                10,                             // id
                                "Inception",                    // nombre
                                "Ciencia ficción",              // descripción
                                Date.valueOf("2025-09-09"),
                                3000L,                          // tarifaxDia
                                "DVD",                          // formatoRenta
                                "SciFi"                         // género
                            );
        im.insertarItem(nuevo);
        System.out.println("Item insertado correctamente: " + nuevo.getNombre());

        // Consultar Items
        Item item = im.consultarItem(2);
        System.out.println("Item consultado: " + item.getNombre());
        List<Item> todos = im.consultarItems();
        System.out.println("Todos los items en DB:");
        todos.forEach(i -> System.out.println(" - " + i.getNombre()));

        // Insertar un nuevo tipo
        tim.addTipoItem("Documental");
        System.out.println("Nuevo tipo agregado: Documental");

        // Consultar todos
        List<TipoItem> tipos = tim.getTiposItems();
        System.out.println("Tipos disponibles:");
        tipos.forEach(t -> System.out.println(" - " + t.getDescripcion()));

        // Consultar por id
        TipoItem tipo_consulta = tim.getTipoItem(1);
        System.out.println("Tipo consultado: " + tipo_consulta.getDescripcion());
                
        sqlss.commit();
        sqlss.close(); 
    }
}
