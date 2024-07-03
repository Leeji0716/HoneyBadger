'use client'

interface pageInterface {
  children: React.ReactNode,
  classname?: string,
  // user: any,
  // setUser: (e:any)=>void
}

export default function Main(props: Readonly<pageInterface>) {
  return (
    <main id='main' className={'min-h-screen flex flex-col items-center realtive ' + props.classname}>
      <header></header>
      {props?.children}
      <footer></footer>
    </main>
  );
}
