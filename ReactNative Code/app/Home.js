import * as SplashScreen from "expo-splash-screen";
import { StyleSheet, Text, View, TouchableOpacity } from "react-native";
import { LinearGradient } from "expo-linear-gradient";
import { useFonts } from "expo-font";
import { useEffect, useState } from "react";
import { Image } from "expo-image";
import { StatusBar } from "react-native";
import { router } from "expo-router";
import LottieView from "lottie-react-native";

SplashScreen.preventAutoHideAsync();

export default function home() {
  const [distance, setDistance] = useState(false);
  const [error1, setError1] = useState(null);
  const [gettotal, settotal] = useState("0");
  const [getSlot, setSlot] = useState("0");

  

  useEffect(() => {
    const ws = new WebSocket(
      "ws://special-lamprey-charmed.ngrok-free.app/EZPark/distance"
    );

    ws.onopen = () => {
      console.log("WebSocket connection opened");
    };

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        setDistance(data.distance);
        console.log(data.distance)
      } catch (err) {
        console.error("Error parsing message:", err);
      }
    };

    ws.onerror = (e) => {
      setError1(e.message);
      console.error("WebSocket error:", e.message);
    };

    ws.onclose = () => {
      console.log("WebSocket connection closed");
    };

    return () => {
      ws.close();
    };
  }, []);

  useEffect(() => {
    async function totalPrice() {
      let response = await fetch(
        "https://special-lamprey-charmed.ngrok-free.app/EZPark/totalPrice"
      );
      if (response.ok) {
        //convert to js object
        let json = await response.json();
        if (json.success) {
          settotal(json.total_price);
        } else {
        }
      }
    }
    totalPrice();
  }, []);
  useEffect(() => {
    async function parkingSlot() {
      let response = await fetch(
         "https://special-lamprey-charmed.ngrok-free.app/EZPark/parkingSlot?data=0"
      );
      if (response.ok) {
        //convert to js object
        let json = await response.json();
        if (json.success) {
          setSlot(json.slot);
        } else {
        }
      }
    }
    parkingSlot();
  }, []);

  const logoPath1 = require("../assets/Images/parking.png");

  return (
    <LinearGradient colors={["#FFF9B0", "#fff"]} style={styleSheet.Lview1}>
      <StatusBar backgroundColor={"#fff"} />
      

      <View style={styleSheet.header}>
        <View style={styleSheet.profileInfo}>
          <Image source={logoPath1} style={styleSheet.avatar} />
          <View style={styleSheet.userDetails}>
            <Text style={styleSheet.accountText}>EZ-Park</Text>
          </View>
        </View>
      </View>
      {distance !== false ? (
        <View style={styleSheet.alertView}>
          <LottieView
            autoPlay
            style={{
              width: 320,
              height: 200,
            }}
            source={require("../assets/Animation/Alert.json")}
          />
        </View>
      ) : (
        <View style={styleSheet.alertView}>
          <Text style={styleSheet.alertText}>No Vehical Signal !! </Text> 
        </View>
      )}

      <View style={styleSheet.view3}>
        <View style={styleSheet.countGroup}>
          <View style={styleSheet.slotCount}>
            <Text>Slot Count</Text>
            <Text>{getSlot}/10</Text>
          </View>
          <View style={styleSheet.priceCount}>
            <Text>Total Price</Text>
            <Text>${gettotal}</Text>
            {/* <Text>${gettotal}</Text> */}
          </View>
        </View>
        <TouchableOpacity
          onPress={() => {
            router.push("/CheckIn");
          }}
        >
          <View style={styleSheet.check}>
            <Text>Check In</Text>
          </View>
        </TouchableOpacity>

        <TouchableOpacity
          onPress={() => {
            router.push("/CheckOut");
          }}
        >
          <View style={styleSheet.check}>
            <Text>Check Out</Text>
          </View>
        </TouchableOpacity>
        <TouchableOpacity
          onPress={() => {
            router.push("/VipInterface");
          }}
        >
          <View style={styleSheet.check}>
            <Text>VIP Member</Text>
          </View>
        </TouchableOpacity>
       
      </View>
    </LinearGradient>
  );
}
const styleSheet = StyleSheet.create({
  header: {
    flexDirection: "row",
    justifyContent: "center",
    columnGap: 20,
    paddingHorizontal: 20,
    alignItems: "center",
    marginBottom: 10,
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
    height:200
  },
  alertText:{
    fontSize:20,
    color:303030,
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

  view3: {
    backgroundColor: "white",
    width: "100%",
    height: 400,
    // borderRadius: 40,
    borderTopLeftRadius: 40,
    borderTopRightRadius: 40,
    padding: 20,
    marginTop: -25,

    elevation: 5,

    // borderWidth: 5,
  },
  countGroup: {
    marginVertical: 40,
    flexDirection: "row",
    columnGap: 20,
    justifyContent: "space-around",
  },
  slotCount: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
    backgroundColor: "#FFB84D",
    borderRadius: 20,
  },
  priceCount: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
    backgroundColor: "#FFB84D",
    borderRadius: 20,
  },
  check: {
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
    backgroundColor: "#FFB84D",
    borderRadius: 20,
    marginBottom: 10,
  },
});
