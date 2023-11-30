# spring-restapi

 # How To Run
 1. Setting koneksi database PostgreSQL lewat application.properties dan sesuaikan port url, username, password sesuai setting masing - masing
 2. Pastikan ada database bernama postgres di PostgreSQL
 3. Run beberapa Query berikut untuk membuat beberapa procedure dan function yang dibutuhkan
# 3.1 Procedur Insert Salary

    CREATE OR REPLACE PROCEDURE public.insert_salary(IN no_emp integer, IN salary integer,
    IN from_date date, IN to_date date)
       LANGUAGE sql
      AS $procedure$
      insert into salaries values(no_emp,from_date , salary , to_date)
      $procedure$
      ;
    
# 3.2 Procedure Update Salary
    
    CREATE OR REPLACE PROCEDURE public.update_salary(IN new_salary integer,
    IN no_emp integer, IN new_from_date date, IN         new_to_date date)
     LANGUAGE sql
      AS $procedure$
      update salaries 
      set salary = new_salary,
      from_date = new_from_date,
      to_date = new_to_date
      where emp_no = no_emp;
      $procedure$
    ;

# 3.3 Procedure Delete Salary

    CREATE OR REPLACE PROCEDURE public.delete_salary(IN no_emp integer)
     LANGUAGE sql
      AS $procedure$
      delete from salaries
      where emp_no = no_emp
      $procedure$
      ;
    
# 3.4 Function Insert Title
	CREATE OR REPLACE FUNCTION public.insert_title(no_emp integer, from_date date, title character varying, to_date date)
 	RETURNS void
 	LANGUAGE plpgsql
	AS $function$
	begin
	
	insert into titles(emp_no,from_date,title,to_date) 
	values (no_emp, from_date,title, to_date);
	end;
	$function$
	;




# 3.5 Function Update Title
	CREATE OR REPLACE FUNCTION public.update_title(no_emp integer, new_from_date date,
     	new_title character varying, new_to_date date)
    	RETURNS void
   	LANGUAGE plpgsql
    	AS $function$
    	begin
	update titles 
	set title = new_title,
	from_date = new_from_date ,
	to_date = new_to_date
	where emp_no = no_emp; 	
 	end;
  	$function$
  	;

# 3.6 Function Delete Title

	CREATE OR REPLACE FUNCTION public.delete_title(no_emp integer)
 	RETURNS void
 	LANGUAGE plpgsql
	AS $function$
	begin
	delete from titles
	where emp_no = no_emp;
		
	end;
	$function$
	;


4. Jika sudah maka running aplikasi springboot dan aplikasi akan berjalan di url: localhost:8080
5. Aplikasi siap di uji dengan postman atau swagger ui

# Unit Test
1. Pastikan setting berikut spring.jpa.hibernate.ddl-auto= create-drop yang ada di application.properties TIDAK DI COMMENT
2. Kemudian running skenario test yang ada
3. Jika running skenario secara bersama - sama. Maka mungkin ada 1 atau 2 skenario yang gagal
4. Jika ini terjadi dimohon tidak mengulang test skenario dari awal tapi tekan Rerun Failed Test (tombol lingkaran merah dengan segitiga hijau yang ada di kiri bawah jendela Intelij)
5. Mohon lakukan Rerun Failed Test 2 - 3 kali sampai semua skenario lolos
6. Kondisi skenario yang gagal saat run test bersama - sama, mungkin dikarenakan ada perubahan data yang disebabkan oleh skenario lain sehingga membuat ada skenario yang gagal
7. Jika running skenario satu per satu maka test nya dapat berjalan dengan lancar

# Documentation
1. Documentasi Colection Postman di https://drive.google.com/file/d/1QddlqDMB8RZVDWBICSLODpExN0RY-3ac/view?usp=sharing
2. Lewat Swagger ui, yang dapat di akses di http://localhost:8080/swagger-ui/index.html
