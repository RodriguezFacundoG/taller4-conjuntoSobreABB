package aed;

import java.util.*;

// Todos los tipos de datos "Comparables" tienen el método compareTo()
// elem1.compareTo(elem2) devuelve un entero. Si es mayor a 0, entonces elem1 > elem2
public class ABB<T extends Comparable<T>> implements Conjunto<T> {
    private Nodo raiz; //Este es el único indispensable, los demás son solo con fines de reducir complejidad
    private int cardinal;

    private class Nodo {
        T valor;
        Nodo izq;
        Nodo der;
        Nodo padre;

        Nodo(T v){
            this.valor = v;
            this.izq = null;
            this.der = null;
            this.padre = null;
        }
    }

    public ABB() {
        this.raiz = null;
        this.cardinal = 0;
    }

    public int cardinal() {
        return this.cardinal;
    }

    public T minimo(){
        Nodo actual = raiz;
        if(raiz != null){
            while(actual.izq != null) {
                actual = actual.izq;
            }
        }
        return actual.valor;
    }

    public T maximo(){
        Nodo actual = raiz; //Asumo que tiene al menos un elemento
        while(actual.der != null){
            actual = actual.der;
        }
        return actual.valor;
    }
    public Nodo buscarNodo(T elem){ //Va a devolver el nodo padre donde tengo que hacer la insercion
        Nodo actual = raiz;
        boolean encontrePadre = false;
        
        while (actual != null && !encontrePadre){
            if(elem.compareTo(actual.valor) > 0 && actual.der != null){
                actual = actual.der;
            } else if (elem.compareTo(actual.valor) < 0 && actual.izq != null) {
                actual = actual.izq;
            } else { //Esto corta cuando lo encuentra. En ese caso pongo el flag en true y en la prox iteracion no entra al while.
                encontrePadre = true;                       
            }
        }    
        return actual;       

        /*
         ### OTRA forma de implementarlo sin flag, pero retornando el nodo actual si lo encuentra, cuando son iguales (caso else)
         Nodo actual = raiz;
         Nodo padre = null;
         while (actual != null) {
            padre = actual;
            if (elem.compareTo(actual.valor) > 0) {
                actual = actual.der;
            } else if (elem.compareTo(actual.valor) < 0) {
                actual = actual.izq;
            } else {
                return actual; //El nodo ya existe en el arbol
            }
        }
        return padre; 
        */
        
    }
    public void insertar(T elem){
        Nodo aux;
        Nodo nuevoNodo;
        if(!pertenece(elem)){
            if(raiz == null){
                raiz = new Nodo(elem);
                cardinal ++;
            } else { //Si no es nulo
                aux = buscarNodo(elem);  //luego tengo que definir donde creo el nuevo nodo con el nuevo elemento
                if(elem.compareTo(aux.valor) > 0){
                    nuevoNodo = new Nodo(elem);
                    nuevoNodo.padre = aux;
                    aux.der = nuevoNodo; //tambien puedo hacer nuevoNodo.padre.der = nuevoNodo
                    cardinal ++;
                } else {
                    nuevoNodo = new Nodo(elem);
                    nuevoNodo.padre = aux;
                    aux.izq = nuevoNodo; //tambien puedo hacer nuevoNodo.padre.izq = nuevoNodo;
                    cardinal ++;
                }
            }
        }
    }

    public boolean pertenece(T elem){
        ABB<T> aux = new ABB<T>();
        boolean res = false;
        Nodo actual = raiz;
        if(this.raiz != null){ //Si la raiz es null, no se va actualiza el valor de res y da false.
            if(elem.compareTo(actual.valor) == 0){
                res = true;
            } else if (elem.compareTo(actual.valor) > 0){ //Si elem > actual.valor, voy a la rama derecha
                aux.raiz = actual.der;
                res = res || aux.pertenece(elem);
            } else if (elem.compareTo(actual.valor) < 0){ //cuando elem < actual.valor, voy a la rama izq
                aux.raiz = actual.izq;
                res = res || aux.pertenece(elem);
            }
        }
        return res;
    }

    public void eliminar(T elem){
        if(pertenece(elem)){
            Nodo actual = buscarNodo(elem);
            if(actual.izq == null && actual.der == null){ //No tiene hijos el elemento a eliminar y tiene padre       
                if(actual.padre != null){
                    Nodo padre = actual.padre;
                    actual = padre;
                    if(elem.compareTo(actual.padre.valor) > 0){ //Si el elemento está en la rama derecha
                        actual.der = null;
                        cardinal --;
                    } else { //Sino está en la rama izquierda.
                        actual.izq = null;
                        cardinal --;
                    }
                } else { //Si estoy parado en la raiz
                    raiz = null;
                    cardinal --;
                }    
            }
            else if(actual.izq == null && actual.der != null){ /* ### SI TIENE UN HIJO DERECHO ### */
                if(actual.padre != null){
                    Nodo padre = actual.padre;
                    if(elem.compareTo(padre.valor) > 0){
                        actual.padre.der = actual.der;
                        actual.der.padre = actual.padre;
                        cardinal --;
                    } else {
                        actual.padre.izq = actual.der;
                        actual.izq.padre = actual.padre;
                        cardinal --;
                    }
                } else { //Si estoy parado en raiz y tiene hijo derecho
                    (raiz.der).padre = null;
                    raiz = raiz.der;
                    cardinal --;
                }
            }
            else if(actual.izq != null && actual.der == null){ /* ### SI TIENE UN HIJO IZQUIERDO ### */
                if(actual.padre != null) {
                    Nodo padre = actual.padre;
                    if(elem.compareTo(padre.valor) > 0){ 
                        actual.padre.der = actual.izq; 
                        actual.izq.padre = actual.padre;
                        cardinal --;
                    } else {
                        actual.padre.izq = actual.izq;
                        actual.izq.padre = actual.padre;
                        cardinal --;
                    }
                } else {
                    (raiz.izq).padre = null;
                    raiz = raiz.izq;
                    cardinal --;
                }
            }
            else{ //Ambos nodos distintos de nulo
                Nodo sucesor = sucesor(actual);
                Nodo padreSucesor = sucesor.padre;
                actual.valor = sucesor.valor; //copio el valor del nodo sucesor encontrado, ahora tengo que eliminarlo:
                //tengo que ver si sucesor es el hijo izquierdo o derecho de padreSucesor
                padreSucesor.izq = null; //Pero siempre es el izquierdo por como está hecho el metodo sucesor
                cardinal --;
            }
        }
    }
    private Nodo sucesor(Nodo nodo){
        // caso tiene subarbol derecho
        Nodo res;
        if (nodo.der != null){
            res = nodo.der;
            while (res.izq != null){
                res = res.izq;
            }
        } else {
        // caso contrario: no tiene subarbol derecho
        res = nodo.padre;   
            while (res.der != null && res.der.valor.equals(nodo.valor)) { //Corta cuando el padre del nodo, no tiene subarbol derecho. Por lo que si tengo solo arbol a la izquierda, el padre es el sucesor.
                res = res.padre;
            }
        }
        return res;
    }

    public String toString(){
        Iterador<T> it = iterador();
        boolean esPrimero = true;
        String res = "{";
        while(it.haySiguiente()){
            if(esPrimero){                
                res = res + it.siguiente();
                esPrimero = false;
            } else {
                res = res + it.haySiguiente() + " ,";
            }
        }
        res += "}";
        return res;
    }

    private class ABB_Iterador implements Iterador<T> {
        private Nodo _actual;
        
        public ABB_Iterador(){
            this._actual = buscarNodo(minimo());
        }
        
        public boolean haySiguiente() {     
            boolean res = false;
            if(sucesor(_actual) != null){
                res = true;
            }
            return res;
        }
    
        public T siguiente() {
            Nodo res = _actual;
            _actual = sucesor(_actual);
            return res.valor;
        }
    }

    public Iterador<T> iterador() {
        return new ABB_Iterador();
    }

}