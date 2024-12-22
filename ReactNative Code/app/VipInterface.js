import * as SplashScreen from "expo-splash-screen";
import {
  StyleSheet,
  Text,
  TextInput,
  View,
  TouchableOpacity,
  Modal,
  Alert,
} from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { useFonts } from "expo-font";
import { useEffect, useState } from "react";
import { Image } from "expo-image";
import { StatusBar } from "react-native";
import Icon from "react-native-vector-icons/Ionicons";
import { router } from "expo-router";
import LottieView from "lottie-react-native";

SplashScreen.preventAutoHideAsync();

export default function home() {
  const [getview, setview] = useState(false);
  // const [getvehhicalNumber, setvehicalNumber] = useState("");

  const logoPath1 = require("../assets/Images/parking.png");

  useEffect(() => {
    const ws = new WebSocket(
      "ws://special-lamprey-charmed.ngrok-free.app/EZPark/rfid"
    );

    ws.onopen = () => {
      console.log("WebSocket connection opened");
    };

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        // setDistance(data.distance);
        console.log(data.Uid);
        Alert.alert("message", "Valid Member, User ID is = " + data.Uid);
        setview(data.view);
      } catch (err) {
        console.error("Error parsing message:", err);
      }
    };

    ws.onerror = (e) => {
      // setError(e.message);
      console.error("WebSocket error:", e.message);
    };

    ws.onclose = () => {
      console.log("WebSocket connection closed");
    };

    return () => {
      ws.close();
    };
  }, []);

  return (
    <LinearGradient colors={["#FFF9B0", "#fff"]} style={styleSheet.Lview1}>
      <StatusBar backgroundColor={"#fff"} />

      <View style={styleSheet.header}>
        <View>
          <TouchableOpacity
            onPress={() => {
              router.push("/Home");
            }}
          >
            <Icon name="arrow-back" size={24} color="#555" />
          </TouchableOpacity>
        </View>
        <View style={styleSheet.profileInfo}>
          <Image
            source={require("../assets/Images/parking.png")}
            style={styleSheet.avatar}
          />
          <View style={styleSheet.userDetails}>
            <Text style={styleSheet.accountText}>VIP Members</Text>
          </View>
        </View>
      </View>
      <View style={styleSheet.View1}>
        {getview !== false ? (
           <><View style={styleSheet.alertView}>
            <LottieView
              autoPlay
              style={{
                width: 320,
                height: 200,
              }}
              source={require("../assets/Animation/done.json")} />
          </View><View style={styleSheet.gate}>
              <TouchableOpacity
                style={styleSheet.inputView3}
                onPress={async () => {
                  let response = await fetch("http://192.168.11.57?status=90");
                  Alert.alert("Message", " Success!");
                  if(response.ok){

                  }
                } }
              >
                <Text>Gate Open</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={styleSheet.inputView3}
                onPress={async () => {
                  let response = await fetch("http://192.168.11.57?status=70");
                  if(response.ok){
                    
                  }
                } }
              >
                <Text>Gate Close</Text>
              </TouchableOpacity>
            </View>
            <TouchableOpacity
              onPress={async () => {
                let response = await fetch("https://special-lamprey-charmed.ngrok-free.app/EZPark/parkingSlot?data=1"
                );

                if (response.ok) {
                  //convert to js object
                  let json = await response.json();
                  if (json.success) {
                    Alert.alert("Message", "Parking Slot Reseved !");
                  } else {
                    Alert.alert("Message", json.message);
                  }
                }
              } }
            >
              <View style={styleSheet.inputView2}>
                <Text>Parking Slot Reseved</Text>
              </View>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={async () => {
                let response = await fetch("https://special-lamprey-charmed.ngrok-free.app/EZPark/parkingSlot?data=2"
                );

                if (response.ok) {
                  //convert to js object
                  let json = await response.json();
                  if (json.success) {
                    Alert.alert("Message", "Parking Slot Open!");
                  } else {
                    Alert.alert("Message", json.message);
                  }
                }
              } }
            >
              <View style={styleSheet.inputView2}>
                <Text>Parking Slot Open</Text>
              </View>
            </TouchableOpacity></>
        ) : (
          <View style={styleSheet.alertView}>
            <LottieView
              autoPlay
              style={{
                width: 320,
                height: 200,
              }}
              source={require("../assets/Animation/search.json")}
            />
          </View>
        )}

       
      </View>
    </LinearGradient>
  );
}
const styleSheet = StyleSheet.create({
  header: {
    flexDirection: "row",
    // justifyContent: 'space-between',
    columnGap: 20,
    paddingHorizontal: 20,
    alignItems: "center",
    marginBottom: 50,
    backgroundColor: "#fff",
    paddingVertical: 10,
    height: 80,
    elevation: 5,
    zIndex: 1,
    borderBottomRightRadius: 20,
    borderBottomLeftRadius: 20,
  },
  alertView: {
    // marginBottom: 30,
    justifyContent: "center",
    alignItems: "center",
    marginVertical: 70,
  },
  profileInfo: {
    flexDirection: "row",
    alignItems: "center",
  },
  userDetails: {
    marginLeft: 10,
  },
  avatar: {
    width: 40,
    height: 40,
  },
  accountText: {
    fontSize: 20,
    // color: "#662d91",
  },

  textInput: {
    borderColor: "yellow",
    borderWidth: 2,
    padding: 10,
    borderRadius: 20,
    marginBottom: 10,
  },
  inputView1: {},
  inputView2: {
    // flex:1,
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
    backgroundColor: "#FFB84D",
    borderRadius: 20,
    marginVertical: 20,
  },
  inputView3: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
    backgroundColor: "#FFB84D",
    borderRadius: 20,
    marginVertical: 20,
  },
  View1: {
    padding: 20,
  },
  gate: {
    // marginVertical: 10,
    flexDirection: "row",
    columnGap: 15,
    justifyContent: "center",
  },
});
