#include <stdio.h>
#include <stdlib.h>

// funzioni da implementare
float** read_grades(int n_students, int n_courses);
float** student_maximums(float** grades,  int n_students, int n_courses);
float* course_averages(float** grades, int n_students, int n_courses);


// funzione di stampa voti medi con header (matricola, voto)
void print_students(float **M, int rows, int columns) {
	int i, j;
	printf("%15s%15s\n","Matricola","Voto");
	for (i=0; i<rows; i++) {
		float *row = M[i];
		for (j=0; j<columns-1; j++)
			printf("%15.2f ", row[j]);
		printf("%15.2f\n", row[columns-1]);
	}
}

// funzione di stampa voti, con il nome del corso nel header
void print_courses(float *grades, char** course_names,int size){
	int i;
	for (i=0; i<size; i++) {
		printf("%15s ", course_names[i]);
	}
	printf("\n");
	for (i=0; i<size; i++) {
		printf("%15.2f ", grades[i]);
	}
	printf("\n");
}



int main() {
	//numero di studenti
	int n_students;
	
	//numero di corsi
	const int n_courses=5;
	
	//nomi dei corsi - gia definiti
	char* course_names[]={"Mathematics","Physics","Programming","Chemistry","English"};
	
	
	//voti degli studenti - una matrice dove 
	//ogni riga rappresenta uno studente
	//la prima colonna rapresenta la matricola dello studente
	//e il resto delle  colonne rapresentano i corsi
	float** student_grades;
	
	//voti massimi degli studenti - una matrice con 2 colonne e n_students righe
	//la prima colonna contiene  la matricola, la seconda il voto massimo
	//ogni riga corrisponde ad uno studente
	float** student_max;
	
	//voti medi per ogni corso
	float* avg_grades;
	
	// leggo il numero di studenti (n_students>0)
	scanf("%d", &n_students);
	if(n_students<=0) return 0;

	// leggo le matricole e i voti degli studenti
	student_grades=read_grades(n_students, n_courses);

	// calcolo i voti massimi degli studenti
	student_max = student_maximums(student_grades, n_students,n_courses);

	// stampo i voti medi
	printf("Voti massimi:\n");
	print_students(student_max, n_students,2);

	// calcolo il voto medio per ogni corso
	avg_grades = course_averages(student_grades, n_students,n_courses);

	// stampo voti medi per ogni corso
	printf("Voti medi per corso:\n");
	print_courses(avg_grades,  course_names,n_courses);

	return 0;
}

float** read_grades(int n_students, int n_courses){
    float **A;
    int   r,c;
    
    A=(float**) malloc(sizeof(float*)*n_students);
    for(r=0;r<n_students;r++)
        A[r]=(float*) malloc(sizeof(float)*(n_courses+1));
        
        
    for(r=0;r<n_students;r++)
        for(c=0;c<(n_courses+1);c++)
            scanf("%f",&A[r][c]);


    return A;

}

float** student_maximums(float** grades,  int n_students, int n_courses){
    float **A,max;
    int   r,c;

    A=(float**) malloc(sizeof(float*)*n_students);
    for(r=0;r<n_students;r++){
        A[r]=(float*) malloc(sizeof(float)*2);
        A[r][0]= grades[r][0];
    }       
        
    for(r=0;r<n_students;r++){
        max=grades[r][1];
        for(c=2;c<(n_courses+1);c++)
            if(grades[r][c] > max)  max=grades[r][c];
        A[r][1]=max;
    }
    
    return A;
}

float* course_averages(float** grades, int n_students, int n_courses){
    float *avg,media;
    int r,c;
    
    avg=(float*) malloc(sizeof(float)*n_courses);

    for(c=1;c<(n_courses+1);c++){
        media=0.0;
        for(r=0;r<n_students;r++)
            media += grades[r][c];
    
        media=media/n_students;
        avg[c-1]=media;
    } 

    return avg;
}


