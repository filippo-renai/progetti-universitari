#include<stdio.h>
#include<stdlib.h>
#include<string.h>

//struttura albero
typedef struct _node{
    char key[101];
    struct _node *left;
    struct _node *right;
}albero;

//struttura d'appoggio
typedef struct{
    char minimo[101];
//per vedere se c'e' stata la visita'
    int visit;    
}cammino;

//funzioni esterne
albero *inserisci_albero(albero *root,char *stringa);
int m(char *u,char *v);
cammino prefix(albero *abr,cammino **les,int *dim);
int confronta(const void *b, const void *c);

int main(){
    char stringa[101];
    int N,i,dim=0;
    albero *root=NULL;
    cammino c,*les=NULL;
    
    scanf("%d",&N);
    for(i=0;i<N;i++){
        scanf("%s",stringa);
        root=inserisci_albero(root,stringa);   
    }
    
    c=prefix(root,&les,&dim); 
    
    qsort(les,dim,sizeof(cammino),confronta);
    
    for(i=0;i<dim;i++)
        printf("%s\n",les[i].minimo);
           
    return 0;
}

//funzione inserimento albero
albero *inserisci_albero(albero *root,char *stringa){
    albero *new=(albero*) malloc(sizeof(albero)),*curr=root,*parent;
    
    strcpy(new->key,stringa);
    new->right=NULL;
    new->left=NULL;
    
    if(root == NULL)  return new;
  
    while(curr != NULL){
        parent=curr;
        if(strcmp(curr->key,stringa) <0)  curr=curr->right;
        else curr=curr->left;
    }
    
    if(strcmp(parent->key,stringa) <0) parent->right=new;
    else parent->left=new;    
    
    return root;   

}

//funzione prefisso:se u e' prefisso di v ritorna 1 senno' 0
int m(char *u,char *v){
    if(strlen(u) > strlen(v)) return 0;
    
    int i=0,dim=strlen(u);
    
    while(i<dim){
        if(u[i] != v[i]) return 0;
        else i++;
    }
    
    return 1;
}

cammino prefix(albero *abr,cammino **les,int *dim){
    cammino cam,ricorda_sx,ricorda_dx;
      
//caso base        
    if(abr->right == NULL && abr->left == NULL){
        (*les)=(cammino*) realloc((*les),sizeof(cammino)*((*dim)+1));        
        strcpy((*les)[(*dim)].minimo,abr->key); 
        (*dim)++;
        strcpy(cam.minimo,abr->key);      
        return cam;
    }

//caso ricorsivo
    if(abr->left != NULL){
        ricorda_sx=prefix(abr->left,les,dim);
        ricorda_sx.visit=1;       
    }
    else    ricorda_sx.visit=0;
    
    if(abr->right != NULL){
        ricorda_dx=prefix(abr->right,les,dim);                     
        ricorda_dx.visit=1;
    }
    else    ricorda_dx.visit=0;

//allora non c'e' stata la visita sinistra 
    if(!ricorda_sx.visit){
        //salvo la stringa
        (*les)=(cammino*) realloc((*les),sizeof(cammino)*((*dim)+1));        
        strcpy((*les)[(*dim)].minimo,abr->key); 
        (*dim)++;
        strcpy(cam.minimo,abr->key);             
        return cam;  
                         
    }
//c'e' stata la visita sinistra    
    else{      
        //salvo la stringa  
        if(m(ricorda_sx.minimo,abr->key)){
            (*les)=(cammino*) realloc((*les),sizeof(cammino)*((*dim)+1));        
            strcpy((*les)[(*dim)].minimo,abr->key); 
            (*dim)++;
        }
        return ricorda_sx;
    }

}

int confronta(const void *b, const void *c){
    cammino *d= (cammino*) b;
    cammino *e= (cammino*) c;
    
    return strcmp(d->minimo,e->minimo);  
}

