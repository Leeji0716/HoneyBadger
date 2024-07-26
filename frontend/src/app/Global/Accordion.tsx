// import React, { useState } from 'react';

// // Accordion Component
// interface AccordionProps {
//     title: string;
//     children: React.ReactNode;
// }

// interface CycleTagResponseDTO {
//     id: number,
//     name: string,
//     color: string
// }

// const Accordion: React.FC<AccordionProps> = ({ title, children }) => {
//     const [isOpen, setIsOpen] = useState(false);

//     const toggleOpen = () => {
//         setIsOpen(!isOpen);
//     };

//     return (
//         <div>
//             <button onClick={toggleOpen} style={buttonStyle}>
//                 {title}
//             </button>
//             {isOpen && <div style={contentStyle}>{children}</div>}
//         </div>
//     );
// };

// // Styles
// const buttonStyle: React.CSSProperties = {
//     display: 'block',
//     width: '100%',
//     padding: '10px',
//     textAlign: 'left',
//     backgroundColor: '#f1f1f1',
//     border: 'none',
//     cursor: 'pointer',
//     fontSize: '16px',
// };

// const contentStyle: React.CSSProperties = {
//     padding: '10px',
//     borderTop: '1px solid #ddd',
//     backgroundColor: '#fafafa',
// };

// // Example usage of Accordion
// const MyComponent: React.FC = () => {
//     const [tagList, setTagList] = useState([
//         { id: 1, name: 'Tag 1', color: '#ff0000' },
//         { id: 2, name: 'Tag 2', color: '#00ff00' }
//     ]);

//     const handleEditTag = (tag: { id: number; name: string; color: string }) => {
//         console.log('Edit tag', tag);
//     };

//     const deleteTag = (id: number) => {
//         return Promise.resolve(); // Simulating tag deletion
//     };

//     const getTagList = (statusId: number) => {
//         return Promise.resolve(tagList); // Simulating getting tag list
//     };

//     const statusid = 1; // Example status ID

//     return (
//         <div>
//             <Accordion title="개인">
//                 {tagList?.map((tag: CycleTagResponseDTO, index: number) => (
//                     <div key={index} className="flex items-center gap-4 p-2 border border-gray-300 rounded-lg shadow-sm mb-2 bg-white hover:bg-gray-50 transition-colors duration-300">
//                         <div className="flex items-center gap-2">
//                             <div
//                                 className="w-4 h-4 rounded-md"
//                                 style={{ backgroundColor: tag.color }}
//                                 aria-label={`Tag color: ${tag.name}`}>
//                             </div>
//                             <span className="text-sm font-medium">{tag.name}</span>
//                         </div>
//                         <div className="flex-grow"></div>
//                         <button
//                             onClick={() => handleEditTag(tag)}
//                             className="px-2 py-1 text-blue-500 hover:bg-blue-100 rounded-md border border-blue-300 text-xs transition-colors duration-300">
//                             수정
//                         </button>
//                         <button
//                             onClick={() => {
//                                 deleteTag(tag.id)
//                                     .then(() => getTagList(statusid))
//                                     .then(r => setTagList(r))
//                                     .catch(e => console.log(e));
//                             }}
//                             className="px-2 py-1 text-red-500 hover:bg-red-100 rounded-md border border-red-300 text-xs transition-colors duration-300">
//                             삭제
//                         </button>
//                     </div>
//                 ))}
//             </Accordion>
//             <Accordion title="그룹">
//                 {tagList.map((tag) => (
//                     <div key={tag.id} className="flex items-center gap-4 p-2 border border-gray-300 rounded-lg shadow-sm mb-2 bg-white hover:bg-gray-50 transition-colors duration-300">
//                         <div className="flex items-center gap-2">
//                             <div
//                                 className="w-4 h-4 rounded-md"
//                                 style={{ backgroundColor: tag.color }}
//                                 aria-label={`Tag color: ${tag.name}`}
//                             ></div>
//                             <span className="text-sm font-medium">{tag.name}</span>
//                         </div>
//                         <div className="flex-grow"></div>
//                         <button
//                             onClick={() => handleEditTag(tag)}
//                             className="px-2 py-1 text-blue-500 hover:bg-blue-100 rounded-md border border-blue-300 text-xs transition-colors duration-300"
//                         >
//                             수정
//                         </button>
//                         <button
//                             onClick={() => {
//                                 deleteTag(tag.id)
//                                     .then(() => getTagList(statusid))
//                                     .then(r => setTagList(r))
//                                     .catch(e => console.log(e));
//                             }}
//                             className="px-2 py-1 text-red-500 hover:bg-red-100 rounded-md border border-red-300 text-xs transition-colors duration-300"
//                         >
//                             삭제
//                         </button>
//                     </div>
//                 ))}
//             </Accordion>
//         </div>
//     );
// };

// export default MyComponent;