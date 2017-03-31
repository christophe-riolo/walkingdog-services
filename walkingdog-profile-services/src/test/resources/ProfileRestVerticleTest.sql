/**
 * Author:  paoesco
 * Created: 31 déc. 2017
 */
INSERT INTO T_USER VALUES ('userwithdog','testUpdateUserOk@hubesco.com','aaaa','aphone',true,'atoken');
INSERT INTO T_DOG VALUES ('mydogtochange','Miko', 'http://preimage','FEMALE','SHIBA_INU','2010-01-01','userwithdog');

INSERT INTO T_USER VALUES ('getImageUserUuid','getImage@hubesco.com','getImage','getImage',true,'getImage');
INSERT INTO T_DOG VALUES ('getImageDogUuid','Miko', 'http://getimage.png','FEMALE','SHIBA_INU','2010-01-01','getImageUserUuid');
