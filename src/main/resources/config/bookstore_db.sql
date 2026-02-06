drop database if exists bookstore_db;
create database bookstore_db character set utf8mb4 collate utf8mb4_unicode_ci;

use bookstore_db;

create table membership_rank (
	rank_id int auto_increment primary key,
    rank_name varchar(50) not null,
    min_point int default 0,
    discount_percent decimal(5, 2) default 0
);

create table role (
	role_id int auto_increment primary key,
    role_name varchar(50) not null unique
);

create table action (
	action_id int auto_increment primary key,
    action_code varchar(50) not null unique,
    action_name varchar(100) not null
);

create table category (
	category_id int auto_increment primary key,
    category_name varchar(100) unique not null
);

create table author (
	author_id int auto_increment primary key,
    author_name varchar(100) not null,
    nationality varchar(100)
);

create table supplier (
	supplier_id int auto_increment primary key,
    supplier_name varchar(100) not null,
    supplier_address varchar(255),
    supplier_phone varchar(20),
    status int default 1
);

create table payment_method (
	payment_method_id int auto_increment primary key,
    payment_method_name varchar(50) not null
);

create table employee (
	employee_id int auto_increment primary key,
    employee_name varchar(100) not null,
    employee_phone varchar(20) unique not null,
    birthday date,
    base_salary decimal(15, 0) default 0,
    salary_factor decimal(5, 2) default 1,
    day_in date,
    status bit default 1,
    role_id int not null,
    foreign key (role_id) references role(role_id)
);

create table account (
	username varchar(50) primary key,
    password varchar(255) not null,
    employee_id int not null unique,
    foreign key (employee_id) references employee(employee_id),
    status int default 1
);

create table permission (
	permission_id int auto_increment primary key,
    role_id int not null,
    action_id int not null,
    is_view bit default 1,
    is_action bit default 1,
    foreign key (role_id) references role(role_id),
    foreign key (action_id) references action(action_id)
);

create table customer (
	customer_id int auto_increment primary key,
    customer_name varchar(100) not null,
    customer_phone varchar(20) unique not null,
    point int default 0,
    rank_id int default 1,
    foreign key (rank_id) references membership_rank(rank_id)
);

create table book (
	book_id int auto_increment primary key,
    book_name varchar(255) not null,
    selling_price decimal(15, 0) default 0,
    quantity int default 0,
	translator text,
    image varchar(255),
    description text,
    status bit default 1,
    category_id int not null,
    tag_detail text,
    supplier_id int not null,
    foreign key (category_id) references category(category_id),
    foreign key (supplier_id) references supplier(supplier_id)
);

create table book_author (
	book_id int not null,
    author_id int not null,
    primary key (book_id, author_id),
    foreign key (book_id) references book(book_id),
    foreign key (author_id) references author(author_id)
);

create table discount (
	discount_id int auto_increment primary key,
    percent decimal(3, 2) not null,
    start_date datetime not null,
    end_date datetime not null,
    status int default 1,
    book_id int not null,
    foreign key (book_id) references book(book_id)
);

create table inventory_log (
	log_id int auto_increment primary key,
    action varchar(50) not null,
    change_quantity int not null,
    remain_quantity int not null,
    reference_id varchar(50),
    created_date datetime default current_timestamp,
    book_id int not null,
    foreign key (book_id) references book(book_id)
);

create table bill (
	bill_id int auto_increment primary key,
    created_date datetime default current_timestamp,
    total_bill_price decimal(15, 0),
    tax decimal(2,2) default 0,
    employee_id int not null,
    customer_id int,
    payment_method_id int not null,
    earned_points int default 0,
    foreign key (employee_id) references employee(employee_id),
    foreign key (customer_id) references customer(customer_id),
    foreign key (payment_method_id) references payment_method(payment_method_id)
);

create table bill_detail (
	bill_id int not null,
    book_id int not null,
    quantity int not null,
    unit_price decimal(15, 0),
    primary key (bill_id, book_id),
    foreign key (bill_id) references bill(bill_id) on delete cascade,
    foreign key (book_id) references book(book_id)
);

create table import_ticket (
	import_ticket_id int auto_increment primary key,
    created_date datetime default current_timestamp,
    total_import_quantity int default 0,
    total_import_price decimal(15, 0),
    status int default 1, -- 0: đã hủy, 1: chờ duyệt, 2: đã nhập
    employee_id int not null,
    supplier_id int not null,
    foreign key (employee_id) references employee(employee_id),
    foreign key (supplier_id) references supplier(supplier_id)
);

create table import_ticket_detail (
	import_ticket_id int not null,
    book_id int not null,
    import_quantity int not null,
    import_price decimal(15, 0),
    primary key (import_ticket_id, book_id),
    foreign key (import_ticket_id) references import_ticket(import_ticket_id) on delete cascade,
    foreign key (book_id) references book(book_id)
);

create table system_parameter (
    param_key varchar(50) primary key,
    param_value varchar(255) not null,
    description varchar(255)
);

create index idx_inventory_date on inventory_log(created_date);
create index idx_inventory_book on inventory_log(book_id);
create index idx_bill_date on bill(created_date);

-- Khởi tạo vài dữ liệu mẫu --
insert into role (role_name) values ('Quản lý'),('Nhân viên bán hàng');

insert into employee (employee_name, employee_phone, birthday, base_salary, day_in, role_id) values
('Quản lý mẫu', '0914349584', '1999-04-15', 12000000, '2025-11-23', 1),
('Nhân viên bán hàng mẫu', '0934129959', '2004-06-18', 7000000, '2025-12-11', 2);

insert into account (username, password, employee_id) values
('admin', 'admin', 1),
('banhang', 'banhang', 2);

insert into supplier (supplier_name, supplier_address, supplier_phone) values
    ('NXB Trẻ', '161B Lý Chính Thắng, Phường Xuân Hoà , TP. Hồ Chí Minh', '0842839316289'),
    ('NXB Văn Học', '18 Nguyễn Trường Tộ, Ba Đình, Hà Nội', '0842437161518'),
    ('NXB Hội Nhà Văn', 'Số 65 Nguyễn Du, Phường Hai Bà Trưng, Hà Nội', '02438222135'),
    ('NXB Dân Trí', 'Số 9, ngõ 26, phố Hoàng Cầu, phường Ô Chợ Dừa, quận Đống Đa, Hà Nội', '02466860751'),
    ('NXB Thanh Niên', 'Toà nhà Ô D29 Phạm Văn Bạch, Yên Hoà, Cầu Giấy, Hà Nội, Việt Nam', '04982526569'),
    ('NXB Công Thương', 'Tòa nhà Bộ Công Thương, 655 Đ. Phạm Văn Đồng, phường Bắc Từ Liêm, Hà Nội', '02439341562'),
    ('Amazon Shop', 'Settle, Washington D.C', '18882804331'),
    ('NXB Tri Thức', 'Tòa nhà Liên hiệp các Hội Khoa học và Kỹ thuật Việt Nam - Lô D20, ngõ 19 Duy Tân, Phường Cầu Giấy, TP. Hà Nội', '02466878415'),
    ('NXB Tổng Hợp TPHCM', '62 Nguyễn Thị Minh Khai, Phường Sài Gòn, TP.HCM', '02838256804'),
    ('NXB Thế Giới', '59 Thợ Nhuộm, Hoàn Kiếm, Hà Nội', '02438253841'),
    ('NXB Lao Động', 'Tầng 12, Số 175 Đường Giảng Võ, Phường Ô Chợ Dừa, Thành phố Hà Nội', '0438515380'),
    ('NXB Kim Đồng', 'Số 55 Quang Trung, Phường Hai Bà Trưng, Thành phố Hà Nội', '01900571595'),
    ('NXB Quân Đội Nhân Dân', '23 Lý Nam Đế, Phường Hoàn Kiếm, Hà Nội', '02438455766'),
    ('NXB Penguin Putnam Inc', '20 Vauxhall Bridge Rd, London, United Kingdom', '18885352334'),
    ('NXB Phụ Nữ', '39 Hàng Chuối, Q. Hai Bà Trưng, Hà Nội', '02439710717'),
    ('NXB Chính Trị Quốc Gia Sự Thật', '6/86 Duy Tân, Cầu Giấy, Hà Nội', '02438221581');

insert into category (category_name) values
    ('Văn học - Nghệ thuật'),
    ('Công nghệ'),
    ('Truyện tranh'),
    ('Kỹ năng sống'),
    ('Giáo dục - Học tập'),
    ('Tâm lý - Khoa học xã hội');

insert into author (author_name, nationality) values
    ('Nguyễn Nhật Ánh', 'Việt Nam'),
    ('Đoàn Minh Phương', 'Việt Nam'),
    ('Alice Munro', 'Canada'),
    ('Lewis Carrol', 'Anh'),
    ('J.R.R. Tolkien', 'Anh'),
    ('Christopher Paolini', 'Mỹ'),
    ('Sir Arthur Conan Doyle', 'Scotland'),
    ('Changwon Pyo ', 'Hàn'),
    ('Mục Qua', 'Việt Nam'),
    ('Nguyên Anh', 'Việt Nam'),
    ('Thương Thái Vi', 'Trung Quốc'),
    ('Di Li', 'Việt Nam'),
    ('Nguyễn Anh Dũng', 'Việt Nam'),
    ('Haruki Murakami', 'Nhật Bản'),
    ('Eran Katz', 'Israel'),
    ('Rosie Nguyễn', 'Việt Nam'),
    ('Paulo Coelho', 'Brazil'),
    ('Dale Carnegie', 'Mỹ'),
    ('Nhiều Tác Giả', ''),
    ('Cao Minh', 'Trung Quốc'),
    ('TS David J. Lieberman', 'Mỹ'),
    ('Diệp Hồng Vũ', 'Trung Quốc'),
    ('Peter Swanson', 'Mỹ'),
    ('Antoine de Saint-Exupéry', 'Pháp'),
    ('Fujiko F Fujio', 'Nhật Bản'),
    ('Tony Buổi Sáng', 'Việt Nam'),
    ('Harper Lee', 'Mỹ'),
    ('Ernest Hemingway', 'Mỹ'),
    ('Victor Hugo', 'Pháp'),
    ('J.K Rowling', 'Anh'),
    ('Margaret Mitchell', 'Mỹ'),
    ('Emily Brontë', 'Anh'),
    ('Andrew Matthews', 'Úc'),
    ('Don Gabor', 'Mỹ'),
    ('Quất Tử Bất Toan', 'Trung Quốc'),
    ('Gosho Aoyama', 'Nhật Bản'),
    ('Lighthouse Writers', 'Việt Nam'),
    ('Dr Lin Lougheed', 'Mỹ'),
    ('Nguyễn Ngọc Anh', 'Việt Nam'),
    ('Lê Bảo Ngọc', 'Việt Nam'),
    ('Baird T.Spalding', 'Mỹ'),
    ('Cảnh Thiên', 'Trung Quốc'),
    ('Shannon Thomas', 'Mỹ'),
    ('Eiichiro Oda', 'Nhật Bản'),
    ('Alexandre Dumas', 'Pháp'),
    ('Akira Toriyama', 'Nhật Bản'),
    ('Chu Lai', 'Việt Nam'),
    ('Miguel de Cervantes Saavedra', 'Tây Ban Nha'),
    ('Nam Cao', 'Việt Nam'),
    ('Vũ Trọng Phụng', 'Việt Nam'),
    ('Ngô Tất Tố', 'Việt Nam'),
    ('Kim Lân', 'Việt Nam'),
    ('Nguyên Hồng', 'Việt Nam'),
    ('Nguyễn Ngọc Tư', 'Việt Nam'),
    ('Đoàn Giỏi', 'Việt Nam'),
    ('Gustave Le Bon', 'Pháp'),
    ('Tô Hoài', 'Việt Nam'),
    ('José Mauro de Vasconcelos', 'Brazil'),
    ('Colleen McCullough', 'Úc'),
    ('Từ Khắc Thành', 'Trung Quốc'),
    ('Thomas H. Cormen', 'Mỹ'),
    ('Charles E. Leiserson', 'Na Uy'),
    ('Ronald L. Rivest', 'Mỹ'),
    ('Clifford Stein', 'Mỹ'),
    ('Erich Gamma', 'Thụy Sĩ'),
    ('Richard Helm', 'Úc'),
    ('Ralph Johnson', 'Mỹ'),
    ('John Vlissides', 'Mỹ'),
    ('Kent Beck', 'Mỹ'),
    ('Peter Norvig', 'Mỹ'),
    ('Stuart Russel', 'Anh'),
    ('Martin Fowler', 'Anh'),
    ('John Brant', 'Mỹ'),
    ('William Opdyke', 'Mỹ'),
    ('Don Roberts', 'Mỹ'),
    ('Pramod Sadalage', 'Mỹ'),
    ('Scott W. Ambler', 'Canada'),
    ('Terry Pratchett', 'Anh'),
    ('Neil Gaiman', 'Anh');

insert into book (book_name, selling_price, quantity, translator, image, description, status, category_id, supplier_id, tag_detail) values
('Bàn Có Năm Chỗ Ngồi', 55000, 0, NULL, NULL, 'Bàn có năm chỗ ngồi xoay quanh câu chuyện tình bạn giữa 5 người bạn. Đó là Huy, Hiền, Quang, Đại, Bảy – họ là năm người bạn với năm cá tính và hoàn cảnh khác nhau cùng chung trong một lớp học. Những trò nghịch ngợm trẻ con đôi khi gây ra mâu thuẫn, nhưng trên tất cả đó là những đứa trẻ ham học, giàu lòng nhân ái và biết quan tâm đến bạn bè. Cảm thông với hoàn cảnh của nhau, từng người nghĩ ra cách giúp đỡ bạn theo khả năng của mình để tình bạn ấy lớn dần theo năm tháng. Mọi thứ trong sách như đưa người đọc được sống lại cái thời còn cắp sách tới trường và tái hiện lại mọi thứ vậy.', 1, 1, 1, 'Truyện dài, Tình bạn, Thiếu nhi, Nguyễn Nhật Ánh'),
('Thằng Quỷ Nhỏ', 70000, 0, NULL, NULL, 'Chuông reo, cả lớp xếp hàng. Nga bước ra khỏi lớp và trước khi đứng vào hàng sau lưng Hạnh, nó khẽ đưa mắt nhìn lướt qua đám con trai xếp hàng kế bên, kín đáo dò xem nhân vật nào là thằng quỷ nhỏ, nhưng nó không thể đoán định được. Những khuôn mặt vui nhộn và rạng rỡ kia chẳng có gì khả nghi. Hay "hắn" mang biệt danh đó là do "hắn" phá phách không ai chịu nổi? Nga thầm nghĩ và lại liếc sang dãy con trai, tò mò quan sát.', 1, 1, 1, 'Truyện dài, Tình bạn, Thiếu nhi, Nguyễn Nhật Ánh'),
('Út Quyên Và Tôi', 45000, 0, NULL, NULL, 'Nếu chẳng may được sinh ra trên cõi đời này, bạn hãy cầu mong mình là người cuối cùng xuất hiện trong gia đình, sau một lô lốc những kẻ làm anh làm chị khác. Đừng nôn nóng, cũng đừng vội vàng. Hãy chờ bọn họ xô đẩy, chen lấn nhau chui ra hết, lúc đó bạn hãy thong thả đặt chân lên mặt đất, ung dung cất tiếng khóc chào đời và hùng hồn tuyên bố:"Ta là con út".', 1, 1, 1, 'Truyện ngắn, Gia đình, Nguyễn Nhật Ánh'),
('Và Khi Tro Bụi', 21500, 0, NULL, NULL, 'Người đàn bà đi tìm cái chết bằng một cách rất lạ, chị sống cuộc sống của một hành khách trên những chuyến tàu xa, và tình cờ được chứng kiến một bi kịch gia đình… Câu chuyện khiến người đọc ngạt thở, chuyện được kể bằng một giọng văn điềm tĩnh, nhưng có thể thấy rõ niềm đau giấu bên trong sự tỉnh táo lành lạnh đó. Niềm đau không khó cắt nghĩa, có thể chia sẻ, nhưng không dễ giải tỏa.', 1, 1, 1, 'Truyện ngắn, Bi kịch'),
('Ghét,Thân,Thương,Yêu,Cưới', 96000, 0, 'Trần Hạnh, Đặng Xuân Thảo, Hạnh Mai', NULL, 'Mỗi khi nhấc một tập truyện mới của Alice Munro lên, người ta luôn biết điều gì đang chờ đợi mình. Vẫn những thị trấn nhỏ và những thành phố mới ở Ontario và British Columbia. Vẫn những người trẻ đầy gai góc và mâu thuẫn. Vẫn những người già đối mặt với cái chết và mối tình muộn. Vẫn giọng kể thấu suốt sắc lạnh của một người quan sát tỉ mỉ tinh tường. Ấy thế nhưng, dường như mỗi trang lại mang đến một loạt những điều ta không hề lường trước: hành động bất ngờ, cảm xúc bất ngờ, ngôn ngữ bất ngờ, chi tiết bất ngờ. Và cốt truyện thì luôn quá tinh vi phức tạp để có thể tóm tắt lại.Hãy đọc chín truyện ngắn trong Ghét Thân Thương Yêu Cưới. Đó là cách duy nhất để thật sự hiểu những điều kỳ diệu mà bậc phù thủy tâm lý và ngôn từ Alice Munro tạo tác nên', 1, 1, 2, 'Truyện ngắn, Tình cảm'),
('Alice ở xứ sở diệu kì - Alice ở xứ sở trong gương', 80000, 0, 'Lê Thị Oanh', NULL, 'Alice ở Xứ Sở thần tiên là cuốn tiểu thuyết thiếu nhi nổi tiếng của nhà văn người Anh, Lewis Carroll. Ngay khi ra đời vào năm 1865, cuốn truyện đã được đông đảo bạn đọc cả trẻ em lẫn người lớn yêu mến. Bảy năm sau, Lewis Carroll cho ra mắt phần tiếp theo, Alice ở xứ sở trong gương, và ngay lập tức những nhân vật như cặp anh em Tweedledum, Hậu Đỏ, Hậu Trắng…của phần này cũng trở nên nổi tiếng không kém Thỏ Trắng, Sâu bướm, nữ Công tước hay Vua Cơ và Hậu Cơ ở phần trước.Nhờ sức hấp dẫn ấy mà trong hơn 130 năm qua, hàng loạt tác phẩm nghệ thuật từ văn học, hội họa, truyền hình, điện ảnh cho đến âm nhạc, game và cả opera đã ra đời dựa trên những cuộc phiêu lưu của cô bé Alice. Và cho đến nay, hai tác phẩm giàu sức tưởng tượng này vẫn là niềm cảm hứng mạnh mẽ cho những người yêu thích văn chương và sáng tạo.', 1, 1, 2, 'Tiểu thuyết, Phiêu lưu, Kì ảo, Kinh điển'),
('Anh Chàng Hobbit', 105000, 0, 'Nguyễn Tâm', NULL, '"Cộng đồng người Anh ngữ được phân làm hai: những người đã đọc anh chàng Hobbit cùng Chúa nhẫn và những người sẽ đọc" - Sunday Times', 1, 1, 3, 'Tiểu thuyết, Phiêu lưu, Giả tưởng'),
('Eragon - Cậu bé cưỡi rồng 1', 72000, 0, 'Đặng Phi Bằng', NULL, 'Tình cờ trong một lần đi săn, Eragon nhặt được một viên đá màu xanh. Tưởng đây là điều may mắn dành cho một đứa trẻ nông dân nghèo khổ như nó, nhưng viên đá, thật ra là một trứng rồng, đã nở ra một rồng con.Cuộc đời đơn giản của Eragon hoàn toàn bị xáo trộn từ sau đêm đó. Cậu bé bị xô đẩy vào một thế giới đầy nguy hiểm của định mệnh, phép thuật và quyền lực.Eragon phải lãnh trách nhiệm của một kỵ sĩ rồng trong huyền thoại, để nắm giữ vận mệnh của vương quốc...', 1, 1, 6, 'Tiểu thuyết, Phiêu lưu, Giả tưởng'),
('Eragon - Cậu bé cưỡi rồng 2', 63000, 0, 'Đặng Phi Bằng', NULL, 'Tình cờ trong một lần đi săn, Eragon nhặt được một viên đá màu xanh. Tưởng đây là điều may mắn dành cho một đứa trẻ nông dân nghèo khổ như nó, nhưng viên đá, thật ra là một trứng rồng, đã nở ra một rồng con.Cuộc đời đơn giản của Eragon hoàn toàn bị xáo trộn từ sau đêm đó. Cậu bé bị xô đẩy vào một thế giới đầy nguy hiểm của định mệnh, phép thuật và quyền lực.Eragon phải lãnh trách nhiệm của một kỵ sĩ rồng trong huyền thoại, để nắm giữ vận mệnh của vương quốc...', 1, 1, 6, 'Tiểu thuyết, Phiêu lưu, Giả tưởng'),
('Eldest - Đại Ca 1', 105000, 0, 'Đặng Phi Bằng', NULL, 'Vừa hoàn tất xong nhiệm vụ giúp quân cách mạng Varden thoát khỏi lực lượng khổng lồ của bạo chúa Galbatorix tiêu diệt, Eragon và nàng rồng Saphira vội vã lên đường tới Ellesméra để được huấn luyện về phép thuật và kiếm thuật – những kỹ năng sinh tử đối với một Kỵ sĩ rồng.Người mới cảnh mới nơi xứ sở thần tiên này, đối với Eragon và Saphira, mỗi ngày là một cuộc phiêu lưu mới. Nhưng sự hỗn loạn và phản bội luôn rình rập khiến Eragon không biết mình phải tin vào ai.Trong thời gian đó, tại quê nhà Carvahall, người anh họ Roran của Eragon lại lâm vào một cuộc chiến khác, một cuộc chiến đẩy Eragon vào vòng nguy hiểm khắc nghiệt hơn.Liệu bàn tay hắc ám của bạo chúa Galbatorix có bóp nghẹt được tất cả những lực lượng chống đối không? Và liệu Eragon có bảo toàn được tính mạng mình không? Mời bạn cùng đọc và khám phá Eldest – Đại ca – Phần tiếp theo của Eragon – Cậu Bé Cưỡi Rồng', 1, 1, 6, 'Tiểu thuyết, Phiêu lưu, Giả tưởng'),
('Eldest - Đại Ca 2', 75000, 0, 'Đặng Phi Bằng', NULL, 'Vừa hoàn tất xong nhiệm vụ giúp quân cách mạng Varden thoát khỏi lực lượng khổng lồ của bạo chúa Galbatorix tiêu diệt, Eragon và nàng rồng Saphira vội vã lên đường tới Ellesméra để được huấn luyện về phép thuật và kiếm thuật – những kỹ năng sinh tử đối với một Kỵ sĩ rồng.Người mới cảnh mới nơi xứ sở thần tiên này, đối với Eragon và Saphira, mỗi ngày là một cuộc phiêu lưu mới. Nhưng sự hỗn loạn và phản bội luôn rình rập khiến Eragon không biết mình phải tin vào ai.Trong thời gian đó, tại quê nhà Carvahall, người anh họ Roran của Eragon lại lâm vào một cuộc chiến khác, một cuộc chiến đẩy Eragon vào vòng nguy hiểm khắc nghiệt hơn.Liệu bàn tay hắc ám của bạo chúa Galbatorix có bóp nghẹt được tất cả những lực lượng chống đối không? Và liệu Eragon có bảo toàn được tính mạng mình không? Mời bạn cùng đọc và khám phá Eldest – Đại ca – Phần tiếp theo của Eragon – Cậu Bé Cưỡi Rồng', 1, 1, 6, 'Tiểu thuyết, Phiêu lưu, Giả tưởng'),
('Brisingr - Hỏa Kiếm 1', 125000, 0, 'Đặng Phi Bằng', NULL, 'Hai Rázac xuất hiện từ một đường hầm. Chúng lăm lăm hai thanh kiếm dài xanh xám, kiểu cổ trong hai tay dị dạng . Không như cha mẹ, Rázac có kích cỡ và hình dạng phỏng theo con người. Một thân hình trơ xương, đen như mun từ đầu tới chân. Chúng tiến ra chớp nhoáng, di chuyển lanh lẹ như một giống côn trùng. Eragon đã không phát hiện ra được bóng dáng của chúng. Hay chúng chỉ là ảo ảnh? Đưa hai tay lên khỏi đầu, Eragon kêu lớn “Brisingr” rồi phóng quả cầu lửa về phía Rázac… Diễn biến tiếp theo của truyện sẽ như thế nào, mời các bạn cùng khám phá trong Brisingr – Hỏa kiếm, phần tiếp theo của Eldest – Đại ca, và cũng là phần 3 của Eragon – Cậu bé cưỡi rồng.', 1, 1, 6, 'Tiểu thuyết, Phiêu lưu, Giả tưởng'),
('Brisingr - Hỏa Kiếm 2', 90000, 0, 'Đặng Phi Bằng', NULL, 'Hai Rázac xuất hiện từ một đường hầm. Chúng lăm lăm hai thanh kiếm dài xanh xám, kiểu cổ trong hai tay dị dạng . Không như cha mẹ, Rázac có kích cỡ và hình dạng phỏng theo con người. Một thân hình trơ xương, đen như mun từ đầu tới chân. Chúng tiến ra chớp nhoáng, di chuyển lanh lẹ như một giống côn trùng. Eragon đã không phát hiện ra được bóng dáng của chúng. Hay chúng chỉ là ảo ảnh? Đưa hai tay lên khỏi đầu, Eragon kêu lớn “Brisingr” rồi phóng quả cầu lửa về phía Rázac… Diễn biến tiếp theo của truyện sẽ như thế nào, mời các bạn cùng khám phá trong Brisingr – Hỏa kiếm, phần tiếp theo của Eldest – Đại ca, và cũng là phần 3 của Eragon – Cậu bé cưỡi rồng.', 1, 1, 6, 'Tiểu thuyết, Phiêu lưu, Giả tưởng'),
('Sherlock Holmes - Toàn tập', 360000, 0, 'Lê Khánh, Đỗ Tư Nghĩa, Vương Thảo, Ngô Văn Quỹ, Lê Nhân, Hoàng Cường, Phạm Quang Trung, Hải Thọ, Bùi Nhật Tân, Thanh Lộc, Minh Phụng', NULL, '"Holmes is a mesmerizing creation and Conan Doyle a master storyteller - The Times"', 1, 1, 2, 'Trinh thám, Toàn tập, Phiêu lưu, Kinh điển'),
('Khách Lạ Và Người Lái Taxi', 53000, 0, NULL, NULL, 'Khách lạ và người lái taxi là cuốn sách tập hợp những câu chuyện kinh dị ngắn của nữ nhà văn Di Li. Mỗi câu chuyện mang mội nội dung riêng biệt, các nhân vật khác biệt, các bối cảnh khác biệt, chúng chỉ có điểm chung là đều nhuốm màu sắc liêu trai, quỷ dị, và thông qua đó, tác giả Di Li muốn hướng người đọc đến hai chữ: “Làm người”.Với lối hành văn mượt mà, trầm bổng, dẫn dắt tình tiết gay cấn và không thiếu những khoảng lặng thắt lòng, cùng các kiến thức xã hội đa dạng, lôi cuốn, chắc chắn nữ nhà văn Di Li sẽ “phù phép” độc giả không thể dời mắt khỏi cuốn sách đầy rẫy ma lực hấp dẫn mà không kém phần hiểm nguy này.', 1, 1, 4, 'Truyện ngắn, Kinh dị, Tâm lý'),
('Tội Phạm Tâm Thần Và Những Lỗ Hổng Công Lý', 140000, 0, 'Bùi Thị Huyền', NULL, 'Bằng kinh nghiệm làm cố vấn tâm lý tội phạm tại Cơ quan Cảnh sát Quốc gia, tác giả Changwon Pyo đã tiết lộ những bí mật, góc khuất xã hội - nơi những kẻ dùng địa vị và quyền lực để thao túng pháp luật. Bạn đọc sẽ đi từ ngạc nhiên, sợ hãi đến phẫn nộ khi lắng nghe nỗi oan bị vùi sâu dưới bóng đen của đồng tiền: trưởng phòng công tố hiếp dâm con gái suốt 12 năm mà không bị tố cáo, mẹ vợ thẩm phán giết người nhưng được “giam lỏng” ở phòng vip bệnh viện, em trai cựu Tổng thống tham nhũng, trốn thuế chỉ phải ngồi tù vỏn vẹn 3 năm…“Không có tiền thì có tội, có tiền thì vô tội.”', 1, 6, 5, 'Tâm lý, Xã hội, Hiện thực, Tội phạm'),
('Hồ Sơ Tâm Lý Học Tâm Thần Hay Kẻ Điên', 189000, 0, 'Tú Phương', NULL, 'Cuốn sách “Hồ sơ tâm lý học - Tâm thần hay kẻ điên” của tác giả Mục Qua sẽ tái hiện chân thực những khốn cảnh tâm lý của những bệnh nhân tâm thần dưới góc nhìn của bác sĩ, cũng như quá trình đấu tranh và hội phục tâm lý của họ. Từ đó phơi bày những góc khuất của xã hội, môi trường gia đình, bạo lực, tội phạm,... mối liên hệ giữa bệnh nhân và xã hội.Cuốn sách bao gồm 13 câu chuyện khác nhau diễn ra trong cùng một bệnh viện tâm thần, nó lột tả chi tiết từng khía cạnh về những căn bệnh tâm thần như chứng cuồng phóng hỏa, rối loạn nhân dạng phân ly, trầm cảm cười, chứng hoang tưởng,....Xuyên suốt cuốn sách bạn sẽ bắt gặp một nhóm “người điên” đang sống trong vực thẳm. Họ có thể là những thiên tài kỳ quái với những ý tưởng kỳ lạ không thể hiểu nổi. Là những kẻ lừa đảo có học, giấu đi nhân cách phản xã hội và phản nhân loại một cách đầy hoàn hảo, càng là những kẻ cô độc đến đáng thương,... Họ mang trong mình những tâm hồn tan nát song đầy nóng bỏng ẩn sau hai từ “kẻ điên”. ', 1, 6, 5, 'Tâm lý, Hiện thực, Xã hội, Tư duy'),
('Đứa Trẻ Hiểu Chuyện Thường Không Có Kẹo Ăn', 148000, 0, 'Nguyệt Lạc', NULL, 'Đứa trẻ hiểu chuyện thường không có kẹo ăn – Cuốn sách dành cho những thời thơ ấu đầy vết thương.Nếu bạn cũng từng là một đứa trẻ như thế, từng phải hạ thấp bản thân, từng buộc phải nhường nhịn người khác, từng phải học cách nhận biết sắc mặt từ khi còn quá nhỏ… thì nhất định đừng bỏ qua cuốn sách “Đứa trẻ hiểu chuyện thường không có kẹo ăn” của tác giả Nguyên Anh.Có thể sau khi đọc xong, những vết thương của bạn vẫn sẽ chẳng thể lành lại vĩnh viễn, nhưng chỉ cần bạn cảm thấy ổn hơn một chút, như vậy là đủ rồi.', 1, 4, 2, 'Tâm lý, Kĩ năng sống'),
('Bến Xe', 76000, 0, 'Greenrosetq', NULL, 'Thứ tôi có thể cho em trong cuộc đời nàychỉ là danh dự trong sạchvà một tương lai tươi đẹp mà thôi.Thế nhưng, nếu chúng ta có kiếp sau,nếu kiếp sau tôi có đôi mắt sáng,tôi sẽ ở bến xe này… đợi em.', 1, 1, 2, 'Tiểu thuyết, Tình cảm, Bi kịch, Xã hội'),
('Tư Duy Ngược', 105000, 0, NULL, NULL, 'Chúng ta thực sự có hạnh phúc không? Chúng ta có đang sống cuộc đời mình không? Chúng ta có dám dũng cảm chiến thắng mọi khuôn mẫu, định kiến, đi ngược đám đông để khẳng định bản sắc riêng của mình không?. Có bao giờ bạn tự hỏi như thế, rồi có câu trả lời cho chính mình?Tôi biết biết, không phải ai cũng đang sống cuộc đời của mình, không phải ai cũng dám vượt qua mọi lối mòn để sáng tạo và thành công… Dựa trên việc nghiên cứu, tìm hiểu, chắt lọc, tìm kiếm, ghi chép từ các câu chuyện trong đời sống, cũng như trải nghiệm của bản thân, tôi viết cuốn sách này.Cuốn sách sẽ giải mã bạn là ai, bạn cần Tư duy ngược để thành công và hạnh phúc như thế nào và các phương pháp giúp bạn dũng cảm sống cuộc đời mà bạn muốn.', 1, 4, 4, 'Kĩ năng sống, Tư duy, Phát triển bản thân'),
('Tư Duy Mở', 105000, 0, NULL, NULL, 'Con người đang sống trong thời đại công nghệ, khi mọi thứ thay đổi chóng mặt, điều đó đòi hỏi chúng ta phải linh hoạt trong cách tư duy để bắt kịp xu hướng toàn cầu. Hay nói cách khác, chúng ta cần có một tư duy mở để đón nhận và khai phá kiến thức mới, bởi nếu chúng ta cứ khăng khăng giữ định kiến của mình thì sự phát triển sẽ đi vào ngõ cụt.Cụ thể hơn, người có tư duy mở tin rằng chỉ cần họ nỗ lực, thay đổi là có thể tiến bộ hơn. Họ sẽ vui vẻ chấp nhận thử thách, xem thử thách như cơ hội để học hỏi được những điều hay cái mới. Khi đối mặt với khó khăn hay không thành công, người có tư duy mở thường có thái độ: “Cách này không hiệu quả, vậy mình thử cách khác”. Đối với họ, thất bại chỉ là bài học giúp họ hoàn hảo hơn trên con đường khẳng định bản thân và phát triển sự nghiệp.Vậy làm thế nào để biết được chúng ta đang có loại tư duy nào, đóng hay mở?Và làm thế nào chúng ta nhận ra chúng?Nhưng làm thế nào để có được tư duy mở?Và tư duy mở góp phần vào cuộc sống của chúng ta thế nào?Khi bạn đặt ra những câu hỏi đó thì cuốn sách này sinh ra để dành cho bạn. Cuốn sách được biên soạn dựa trên sự học tập và nghiên cứu tài liệu trong và ngoài nước cũng như từ những trải nghiệm của bản thân tác giả sẽ mang lại cho bạn những giá trị hữu ích của tư duy mở, giúp bạn tự tin chinh phục ước mơ, sẵn sàng đón nhận mọi chướng ngại và luôn nở nụ cười hạnh phúc.', 1, 4, 4, 'Kĩ năng sống, Tư duy, Phát triển bản thân'),
('Kafka Bên Bờ Biển', 180000, 0, 'Dương Tường', NULL, '"Có những điều một khi đã mất đi thì không bao giờ tìm lại được. Và có những điều, dù có cố gắng đến đâu, cũng không thể trốn tránh."Kafka Bên Bờ Biển không chỉ là hành trình của Kafka Tamura đi tìm bản thân, mà còn là câu hỏi về số phận: Liệu ta có thể chạy trốn định mệnh, hay càng chạy, ta càng lao vào nó?"Sau cơn bão, thế giới xung quanh vẫn vậy. Nhưng cậu thì đã khác rồi."', 1, 1, 2, 'Truyện dài, Kì ảo, Trừu tượng'),
('Trí Tuệ Do Thái', 140000, 0, 'Phương Oanh', NULL, 'Trí tuệ Do Thái là một cuốnsách tư duyđầy tham vọng trong việc nâng cao khả năng tự học tập, ghi nhớ và phân tích - những điều đã khiến người Do Thái vượt trội lên, chiếm lĩnh những vị trí quan trọng trong ngành truyền thông, ngân hàng và những giải thưởng sáng tạo trên thế giới.Tuy là một cuốn sách nhỏ nhưngTrí Tuệ Do Thái lại mang trong mình tri thức về một dân tộc có thể nhỏ về số lượng nhưng vĩ đại về trí tuệ và tài năng. Cuốn sách không chỉ lý giải lý do vì sao những người Do Thái trên thế giới lại thông minh và giàu có, mà còn đặc tả con đường thành công của một người Do Thái - Jerome cùng những triết lý được đúc kết đầy giá trị.', 1, 4, 8, 'Tư duy, Triết lý'),
('Tuổi Trẻ Đáng Giá Bao Nhiêu?', 100000, 0, NULL, NULL, '“Bạn hối tiếc vì không nắm bắt lấy một cơ hội nào đó, chẳng có ai phải mất ngủ.Bạn trải qua những ngày tháng nhạt nhẽo với công việc bạn căm ghét, người ta chẳng hề bận lòng.Bạn có chết mòn nơi xó tường với những ước mơ dang dở, đó không phải là việc của họ.Suy cho cùng, quyết định là ở bạn. Muốn có điều gì hay không là tùy bạn.Nên hãy làm những điều bạn thích. Hãy đi theo tiếng nói trái tim. Hãy sống theo cách bạn cho là mình nên sống.Vì sau tất cả, chẳng ai quan tâm.”', 1, 4, 3, 'Triết lý, Phát triển bản thân'),
('Nhà Giả Kim', 76000, 0, 'Lê Chu Cầu', NULL, '"Nhà Giả Kim" không đơn thuần là một cuốn tiểu thuyết, mà là bản đồ dẫn lối đến giấc mơ, khao khát và định mệnh của mỗi con người. Câu chuyện về chàng trai chăn cừu Santiago không chỉ mang đến những cuộc phiêu lưu hấp dẫn, mà còn mở ra nhiều tầng triết lý sâu sắc về cuộc sống.', 1, 1, 3, 'Tiểu thuyết, Phiêu lưu, Kì ảo, Kinh điển'),
('Đắc Nhân Tâm', 60000, 0, 'Minh Chương', NULL, '“Kiếm một triệu đô la còn dễ hơn tạo ra một cụm từ thông dụng trong tiếng Anh”. “Đắc nhân tâm” đã trở thành một cụm từ yêu thích, một chủ đề bàn luận, một câu trích dẫn thường dùng trong một số bối cảnh từ phim hoạt hình đến những phát biểu, diễn văn chính trị hay tiểu thuyết. Cuốn sách được dịch sang hầu hết các ngôn ngữ phổ biến trên thế giới. Mỗi thế hệ độc giả đều khám phá được những điều mới lạ, những điểm tương đồng và sức hấp dẫn riêng của cuốn sách.', 1, 4, 4, 'Kĩ năng sống, Giao tiếp, Phát triển bản thân'),
('Đắc Nhân Tâm', 70000, 0, 'Trần Cẩm', NULL, 'Đắc nhân tâm của Dale Carnegie là quyển sách của mọi thời đại và một hiện tượng đáng kinh ngạc trong ngành xuất bản Hoa Kỳ. Trong suốt nhiều thập kỷ tiếp theo và cho đến tận bây giờ, tác phẩm này vẫn chiếm vị trí số một trong danh mục sách bán chạy nhất và trở thành một sự kiện có một không hai trong lịch sử ngành xuất bản thế giới và được đánh giá là một quyển sách có tầm ảnh hưởng nhất mọi thời đại.', 1, 4, 2, 'Kĩ năng sống, Giao tiếp, Phát triển bản thân'),
('Tôi thấy hoa vàng trên cỏ xanh', 130000, 0, NULL, NULL, 'Những câu chuyện nhỏ xảy ra ở một ngôi làng nhỏ: chuyện người, chuyện cóc, chuyện ma, chuyện công chúa và hoàng tử , rồi chuyện đói ăn, cháy nhà, lụt lội,... Bối cảnh là trường học, nhà trong xóm, bãi tha ma. Dẫn chuyện là cậu bé 15 tuổi tên Thiều. Thiều có chú ruột là chú Đàn, có bạn thân là cô bé Mận. Nhưng nhân vật đáng yêu nhất lại là Tường, em trai Thiều, một cậu bé học không giỏi. Thiều, Tường và những đứa trẻ sống trong cùng một làng, học cùng một trường, có biết bao chuyện chung. Chúng nô đùa, cãi cọ rồi yêu thương nhau, cùng lớn lên theo năm tháng, trải qua bao sự kiện biến cố của cuộc đời.', 1, 1, 1, 'Tiểu thuyết, Gia đình, Thiếu nhi, Tình cảm, Nguyễn Nhật Ánh'),
('Cho tôi xin một vé đi tuổi thơ', 90000, 0, NULL, NULL, 'Câu chuyện xoay quanh cu Mùi, Tí sún, Hải cò và Tủn - nhóm trẻ con với những trò nghịch ngợm “nhất quỷ, nhì ma”. Dưới góc nhìn hài hước nhưng cũng đầy sâu sắc, Nguyễn Nhật Ánh không chỉ kể về những trò chơi thơ ấu mà còn mở ra cả một thế giới tuổi thơ chân thực: những buổi trốn ngủ trưa đi thả diều, những lần tức tối vì người lớn áp đặt, hay những rung động đầu đời vụng dại.Nhưng tuổi thơ không kéo dài mãi mãi. Khi lớn lên, ta nhận ra điều từng chán ghét lúc bé lại là thứ ta khao khát nhất khi trưởng thành. Cuốn sách không chỉ khiến bạn cười vì những trò nghịch dại, mà còn lắng lại để suy ngẫm: liệu người lớn có thực sự hiểu trẻ con, hay chỉ áp đặt chúng theo cách mình muốn?', 1, 1, 1, 'Tiểu thuyết, Thiếu nhi, Tình cảm, Nguyễn Nhật Ánh'),
('Thiên tài bên trái, Kẻ điên bên phải', 150000, 0, 'Thu Hương', NULL, 'Hỡi những con người đang oằn mình trong cuộc sống, bạn biết gì về thế giới của mình? Là vô vàn thứ lý thuyết được các bậc vĩ nhân kiểm chứng, là luật lệ, là cả nghìn thứ sự thật bọc trong cái lốt hiển nhiên, hay những triết lý cứng nhắc của cuộc đời?Lại đây, vượt qua thứ nhận thức tẻ nhạt bị đóng kín bằng con mắt trần gian, khai mở toàn bộ suy nghĩ, để dòng máu trong bạn sục sôi trước những điều kỳ vĩ, phá vỡ mọi quy tắc. Thế giới sẽ gọi bạn là kẻ điên, nhưng vậy thì có sao? Ranh giới duy nhất giữa kẻ điên và thiên tài chẳng qua là một sợi chỉ mỏng manh: Thiên tài chứng minh được thế giới của mình, còn kẻ điên chưa kịp làm điều đó. Chọn trở thành một kẻ điên để vẫy vùng giữa nhân gian loạn thế hay khóa hết chúng lại, sống mãi một cuộc đời bình thường khiến bạn cảm thấy hạnh phúc hơn?Thiên tài bên trái, kẻ điên bên phải là cuốn sách dành cho những người điên rồ, những kẻ gây rối, những người chống đối, những mảnh ghép hình tròn trong những ô vuông không vừa vặn… những người nhìn mọi thứ khác biệt, không quan tâm đến quy tắc. Bạn có thể đồng ý, có thể phản đối, có thể vinh danh hay lăng mạ họ, nhưng điều duy nhất bạn không thể làm là phủ nhận sự tồn tại của họ. Đó là những người luôn tạo ra sự thay đổi trong khi hầu hết con người chỉ sống rập khuôn như một cái máy. Đa số đều nghĩ họ thật điên rồ nhưng nếu nhìn ở góc khác, ta lại thấy họ thiên tài. Bởi chỉ những người đủ điên nghĩ rằng họ có thể thay đổi thế giới mới là những người làm được điều đó.', 1, 6, 10, 'Tâm lý, Triết lý, Hiện thực, Tư duy');

insert into book_author (book_id, author_id) values
    (1, 1),
    (2, 1),
    (3, 1),
    (4, 2),
    (5, 3),
    (6, 4),
    (7, 5),
    (8, 6),
    (9, 6),
    (10, 6),
    (11, 6),
    (12, 6),
    (13, 6),
    (14, 7),
    (15, 12),
    (16, 8),
    (17, 9),
    (18, 10),
    (19, 11),
    (20, 13),
    (21, 13),
    (22, 14),
    (23, 15),
    (24, 16),
    (25, 17),
    (26, 18),
    (27, 18),
    (28, 1),
    (29, 1),
    (30, 20);