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
            return actual.valor;
        } else {
            return null;
        }
    }

    public T maximo(){
        Nodo actual = raiz; //Asumo que tiene al menos un elemento
        if(raiz!=null){
            while(actual.der != null){
                actual = actual.der;
            }
            return actual.valor;
        } else {
            return null;
        }
    }
    public Nodo buscarNodo(T elem){ //Encuentra el nodo asociado al valor elem. Si no está devuelve al padre para facilitar el metodo insertar
        Nodo actual = raiz;
        boolean encontrePadre = false;
        
        while (actual != null && !encontrePadre){
            if(elem.compareTo(actual.valor) > 0 && actual.der != null){
                actual = actual.der;
            } else if (elem.compareTo(actual.valor) < 0 && actual.izq != null) {
                actual = actual.izq;
            } else { //Corto cuando lo encuentro, poniendo el flag en true para que no entre al while nuevamente. Ó cuando no està el elemento, devuelve el padre
                encontrePadre = true;
            }
        }
        return actual;
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
    /* 
    * 
    */
    public void eliminar(T elem){
        if(pertenece(elem)){
            Nodo actual = buscarNodo(elem); //Ahora actual es el nodo a eliminar
            /*##### NO TIENE HIJOS #####*/
            if(actual.izq == null && actual.der == null){
                if(actual.padre != null){ //SI NO ES LA RAIZ (tiene padre)
                    Nodo padre = actual.padre;                
                    if(elem.compareTo(padre.valor) > 0){ //Si el elemento está en la rama derecha
                        actual.padre = null;
                        padre.der = null;
                        cardinal --;
                    } else { //Sino está en la rama izquierda.
                        actual.padre = null;
                        padre.izq = null;
                        cardinal --;
                    }
                } else { //SINO ES LA RAIZ
                    raiz = null;
                    cardinal --;
                }
            }
            /*##### SI TIENE UN HIJO DERECHO #####*/
            else if(actual.izq == null && actual.der != null){
                Nodo hijo = actual.der;
                if(actual.padre != null){
                    Nodo padre = actual.padre;
                    if(elem.compareTo(padre.valor) > 0){ //si actual es hijo derecho
                        padre.der = actual.der;
                        hijo.padre = actual.padre;                
                        cardinal --;
                    } else { //si actual es hijo izq
                        padre.izq = actual.der;
                        hijo.padre = actual.padre;
                        cardinal --;
                    }
                } else { //Si estoy parado en raiz y tiene hijo derecho
                    hijo.padre = null;
                    raiz = raiz.der;
                    cardinal --;
                }
            }
            /*###### SI TIENE UN HIJO IZQUIERDO #####*/
            else if(actual.izq != null && actual.der == null){
                Nodo hijo = actual.izq;
                if(actual.padre != null) {
                    Nodo padre = actual.padre;
                    if(elem.compareTo(padre.valor) > 0){ 
                        padre.der = actual.izq; 
                        hijo.padre = actual.padre;
                        cardinal --;
                    } else {
                        padre.izq = actual.izq;
                        hijo.padre = actual.padre;
                        cardinal --;
                    }
                } else { //Si estoy parado en la raiz y tiene hijo izquierdo
                    hijo.padre = null;
                    raiz = raiz.izq;
                    cardinal --;
                }
            }
            /*##### TIENE DOS HIJOS #####*/
            else {
                Nodo sucesor = sucesor(actual);
                actual.valor = sucesor.valor; //Copio el valor
                if (sucesor.padre.izq == sucesor) { // Si el sucesor es el hijo izquierdo de su padre
                    sucesor.padre.izq = sucesor.der; 
                } 
                else {                            // Si el sucesor es el hijo derecho de su padre
                    sucesor.padre.der = sucesor.der;
                }
                
                if (sucesor.der != null) {
                    sucesor.der.padre = sucesor.padre;
                }
            
                cardinal--;
            }
        }
    }    

    private Nodo sucesor(Nodo nodo){
        if(nodo == null) { //Si el abb es vacio
            return null;
        } else {
            // caso tiene subarbol derecho
            Nodo res = null;
            if (nodo.der != null){
                res = nodo.der;
                while (res.izq != null){
                    res = res.izq;
                }
            } else {
            // caso contrario: no tiene subarbol derecho
            res = nodo.padre;
                while (res != null && res.der.valor.equals(nodo.valor)) { //Corta cuando el padre del nodo, no tiene subarbol derecho. Por lo que si tengo solo arbol a la izquierda, el padre es el sucesor.
                    nodo = res;
                    res = res.padre;         //En caso de que el padre tenga arbol a la derecha y el hijo derecho sea el nodo al que le busco sucesor, tengo que subir en el arbol
                }                            //En todo momento lo que yo quiero es encontrar un hijo izquierdo, o sea no entrar al while
            }
            return res;
        }
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
                res = res + "," + it.siguiente();
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
            return _actual != null; 
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