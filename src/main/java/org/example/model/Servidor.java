package org.example.model;

public class Servidor {

    private int idServidor;
    private String nome;
    private String matricula;
    private String cpf;
    private String departamento;
    private String cargo;

    public Servidor(int idServidor, String nome,
                    String matricula, String cpf,
                    String departamento, String cargo) {

        this.idServidor = idServidor;
        this.nome = nome;
        this.matricula = matricula;
        this.cpf = cpf;
        this.departamento = departamento;
        this.cargo = cargo;
    }

    public int getIdServidor() {
        return idServidor;
    }

    public String getNome() {
        return nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getCpf() {
        return cpf;
    }

    public String getDepartamento() {
        return departamento;
    }

    public String getCargo() {
        return cargo;
    }

    @Override
    public String toString() {
        return "Servidor: " + nome +
                " | Matrícula: " + matricula +
                " | Departamento: " + departamento +
                " | Cargo: " + cargo;
    }
}
